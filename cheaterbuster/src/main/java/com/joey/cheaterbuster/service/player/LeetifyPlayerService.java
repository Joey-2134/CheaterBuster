package com.joey.cheaterbuster.service.player;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.VaclistProfileDTO;
import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import com.joey.cheaterbuster.dto.leetify.match.StatsDTO;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.dto.leetify.player.TeammateDTO;
import com.joey.cheaterbuster.entity.PlayerData;
import com.joey.cheaterbuster.exception.LeetifyApiException;
import com.joey.cheaterbuster.exception.PlayerNotFoundException;
import com.joey.cheaterbuster.exception.VaclistApiException;
import com.joey.cheaterbuster.mapper.PlayerDataMapper;
import com.joey.cheaterbuster.repository.PlayerDataRepository;
import com.joey.cheaterbuster.service.match.LeetifyMatchService;
import com.joey.cheaterbuster.util.Utils;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeetifyPlayerService {

    private static final int BANNED_PLAYER_GET_COUNT = 100;
    private static final int INITIAL_PAGE = 3490;
    private static final int MAX_PLAYER_IDS_FROM_MATCHES = 20;
    private static final String GET_PROFILE_PATH = "/v3/profile?steam64_id=";
    private static final String GET_BANNED_PATH = "https://vaclist.net/api/banned";
    private final RestTemplate restTemplate;
    private final LeetifyConfig config;
    private final LeetifyMatchService leetifyMatchService;
    private final PlayerDataRepository playerDataRepository;
    private final PlayerDataMapper playerDataMapper;
    private int currentPage = INITIAL_PAGE;

    /**
     * Fetches the profile of a player given their Steam64 ID.
     *
     * @param steam64Id The Steam64 ID of the player
     * @return PlayerDataDTO containing the player's profile information
     */
    public PlayerDataDTO getPlayerProfile(String steam64Id) {
        log.debug("Fetching player profile for Steam ID: {}", steam64Id);

        // Check database first
        Optional<PlayerData> existingPlayer = playerDataRepository.findBySteamId(steam64Id);
        if (existingPlayer.isPresent()) {
            log.info("Player profile found in database for Steam ID: {} ({})", steam64Id, existingPlayer.get().getName());
            return playerDataMapper.toDTO(existingPlayer.get());
        }

        // Not in database, fetch from Leetify API
        log.debug("Player not in database, fetching from Leetify API for Steam ID: {}", steam64Id);
        return fetchFromLeetifyApi(steam64Id);
    }

    /**
     * Fetches player profiles starting from an initial player and traversing through their teammates
     * until the specified number of entries is reached. If player has no recent teammates and Ids to check runs out,
     * it will look at recent matches to find more players.
     *
     * @param numEntries The target number of profiles to fetch
     * @param firstId The Steam64 ID of the initial player
     * @return List of player profiles
     */

    public List<PlayerDataDTO> getPlayerProfiles(int numEntries, String firstId) {
        log.info("Starting players gathering process from Steam ID: {} (target: {} profiles)", firstId, numEntries);
        Set<String> checkedIds = new HashSet<>();
        Set<String> enqueuedIds = new HashSet<>();
        List<PlayerDataDTO> profiles = new ArrayList<>();
        Queue<String> idsToCheck = new LinkedList<>();

        idsToCheck.add(firstId);
        enqueuedIds.add(firstId);

        while (!idsToCheck.isEmpty() && profiles.size() < numEntries) {
            String currentId = idsToCheck.poll();
            log.debug("Processing Steam ID: {} (Progress: {}/{})", currentId, profiles.size(), numEntries);

            PlayerDataDTO profile;
            try {
                profile = getPlayerProfile(currentId);
            } catch (Exception e) {
                log.warn("Failed to fetch profile for Steam ID: {} - {}", currentId, e.getMessage());
                checkedIds.add(currentId);
                continue;
            }

            // If profile couldn't be retrieved, mark checked and continue
            if (profile == null) {
                log.warn("Received null profile for Steam ID: {}, skipping", currentId);
                checkedIds.add(currentId);
                continue;
            }

            // Successfully retrieved
            profiles.add(profile);
            checkedIds.add(currentId);
            log.debug("Added profile for {} (Total: {})", profile.getName(), profiles.size());

            if (profiles.size() >= numEntries) break;

            // Add teammates from profile
            List<String> teammateIds = getRecentTeammateIds(profile);
            boolean anyTeammatesAdded = false;
            for (String teammateId : teammateIds) {
                if (!checkedIds.contains(teammateId) && enqueuedIds.add(teammateId)) {
                    idsToCheck.add(teammateId);
                    anyTeammatesAdded = true;
                }
            }
            if (anyTeammatesAdded) {
                log.debug("Added {} teammates to queue from {}", teammateIds.size(), profile.getName());
            }

            // If no teammates were added, try expanding via recent matches
            if (!anyTeammatesAdded) {
                log.debug("No new teammates found for {}, expanding via recent matches", profile.getName());
                Set<String> matchPlayerIds = getPlayerIdsFromRecentMatches(profile);
                int addedFromMatches = 0;
                for (String id : matchPlayerIds) {
                    if (!checkedIds.contains(id) && enqueuedIds.add(id)) {
                        idsToCheck.add(id);
                        addedFromMatches++;
                    }
                }
                if (addedFromMatches > 0) {
                    log.debug("Added {} player IDs from recent matches", addedFromMatches);
                }
            }
        }

        log.info("Completed player gathering. Collected {} profiles", profiles.size());
        return profiles;
    }

    /**
     * Gets and saves list of banned players to DB with automatic pagination.
     * Only fetches profiles that don't already exist in the database.
     * Fetches from current page and increments page counter for next call.
     *
     * @return list of banned player profiles from current page
     */
    public List<PlayerDataDTO> getBannedPlayerProfiles() {
        log.info("Fetching banned player profiles from VacList page {}", currentPage);
        List<PlayerDataDTO> profiles = new ArrayList<>();
        Set<String> bannedSteamIds = getBannedSteamIds();

        log.info("Retrieved {} Steam IDs from VacList page {}", bannedSteamIds.size(), currentPage);

        int notFoundCount = 0;
        int errorCount = 0;

        for (String steamId : bannedSteamIds) {
            try {
                // getPlayerProfile checks DB first, fetches from API if not present, and saves to DB
                PlayerDataDTO profile = getPlayerProfile(steamId);
                profiles.add(profile);
                log.debug("Added banned player profile: {} (Total: {})", profile.getName(), profiles.size());
            } catch (PlayerNotFoundException e) {
                notFoundCount++;
                log.info("Banned player not found on Leetify for Steam ID: {}, skipping", steamId);
            } catch (Exception e) {
                errorCount++;
                log.warn("Failed to fetch banned player profile for Steam ID: {} - {}", steamId, e.getMessage());
            }
        }

        currentPage++;
        log.info("Completed banned player gathering from page {}. VacList returned: {}, Successfully collected: {}, Not found on Leetify: {}, Errors: {}",
                currentPage - 1, bannedSteamIds.size(), profiles.size(), notFoundCount, errorCount);
        return profiles;
    }

    /**
     * Fetches a list of banned steamIds from vaclist
     *
     * @return a set of steamIds
     */
    @RateLimiter(name = "vaclistApi")
    private Set<String> getBannedSteamIds() {
        Set<String> steamIds = new HashSet<>();
        String url = GET_BANNED_PATH + "?count=" + BANNED_PLAYER_GET_COUNT + "&page=" + currentPage;
        HttpHeaders headers = Utils.createHeadersWithUserAgent(config.getUserAgent());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<VaclistProfileDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VaclistProfileDTO[].class
            );

            VaclistProfileDTO[] profiles = response.getBody();
            if (profiles != null) {
                for (VaclistProfileDTO profile : profiles) {
                    if (profile.getSteamId() != null) {
                        steamIds.add(profile.getSteamId());
                    }
                }
                log.debug("Fetched {} Steam IDs from VacList page {}", steamIds.size(), currentPage);
            }

            return steamIds;
        } catch (HttpClientErrorException e) {
            log.error("VacList API error on page {}: {} - {}", currentPage, e.getStatusCode(), e.getMessage());
            throw new VaclistApiException("Failed to fetch banned Steam IDs from VacList (page " + currentPage + ")", e);
        } catch (Exception e) {
            log.error("Unexpected error fetching banned Steam IDs from page {}: {}", currentPage, e.getMessage(), e);
            throw new VaclistApiException("Unexpected error fetching banned Steam IDs from VacList (page " + currentPage + ")", e);
        }
    }

    /**
     * Fetches player profile from Leetify API.
     *
     * @param steam64Id The Steam64 ID of the player
     * @return PlayerDataDTO containing the player's profile information
     */
    @RateLimiter(name = "leetifyApi")
    private PlayerDataDTO fetchFromLeetifyApi(String steam64Id) {
        String url = config.getBaseUrl() + GET_PROFILE_PATH + steam64Id;

        HttpHeaders headers = Utils.createLeetifyHeaders(config.getApiKey(), config.getUserAgent());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PlayerDataDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlayerDataDTO.class
            );

            PlayerDataDTO profile = response.getBody();
            if (profile != null) {
                log.info("Successfully fetched profile from Leetify API for Steam ID: {} (Name: {})", steam64Id, profile.getName());

                // Save to database
                savePlayerData(profile);

                return profile;
            } else {
                log.error("Received null response body from Leetify API for Steam ID: {}", steam64Id);
                throw new LeetifyApiException("Received null response from Leetify API for Steam ID: " + steam64Id);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Player not found on Leetify API for Steam ID: {}", steam64Id);
            throw new PlayerNotFoundException(steam64Id, e);
        } catch (HttpClientErrorException e) {
            log.error("Leetify API error for Steam ID {}: {} - {}", steam64Id, e.getStatusCode(), e.getMessage());
            throw new LeetifyApiException("Leetify API error for Steam ID " + steam64Id + ": " + e.getStatusCode(), e);
        } catch (LeetifyApiException | PlayerNotFoundException e) {
            // Already our custom exceptions, just rethrow
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error fetching player profile for Steam ID: {}", steam64Id, e);
            throw new LeetifyApiException("Unexpected error fetching player profile for Steam ID: " + steam64Id, e);
        }
    }

    /**
     * Utility method to extract Steam64 IDs from players in recent matches.
     * Stops fetching match details once MAX_PLAYER_IDS_FROM_MATCHES is reached to reduce API calls.
     * @param profile The PlayerDataDTO whose recent matches are to be analyzed
     * @return Set of Steam64 IDs from recent matches
     */
    private Set<String> getPlayerIdsFromRecentMatches(PlayerDataDTO profile) {
        log.debug("Fetching player IDs from recent matches for Steam ID: {}", profile.getSteamId());
        Set<String> playerIds = new HashSet<>();

        List<MatchDTO> recentMatches = leetifyMatchService.getMatchHistory(profile.getSteamId());
        log.debug("Found {} recent matches for {}", recentMatches.size(), profile.getName());

        int matchesProcessed = 0;
        for (MatchDTO match : recentMatches) {
            // Stop fetching match details if we've collected enough player IDs
            if (playerIds.size() >= MAX_PLAYER_IDS_FROM_MATCHES) {
                log.debug("Reached max player IDs limit ({}) after processing {} matches",
                        MAX_PLAYER_IDS_FROM_MATCHES, matchesProcessed);
                break;
            }

            MatchDTO matchDetails = leetifyMatchService.getMatchDetails(match.getMatchId());
            if (matchDetails != null && matchDetails.getStats() != null) {
                matchesProcessed++;
                for (StatsDTO stats : matchDetails.getStats()) {
                    playerIds.add(stats.getSteam64Id());

                    // Check again after adding each player ID
                    if (playerIds.size() >= MAX_PLAYER_IDS_FROM_MATCHES) {
                        break;
                    }
                }
            }
        }

        log.debug("Collected {} unique player IDs from {} matches", playerIds.size(), matchesProcessed);
        return playerIds;
    }

    /**
     * Utility method to extract Steam64 IDs from a player's recent teammates.
     * @param playerData The PlayerDataDTO containing recent teammates
     * @return List of Steam64 IDs
     */
    private List<String> getRecentTeammateIds(PlayerDataDTO playerData) {
        List<TeammateDTO> recentTeammates = playerData.getRecentTeammates();
        if (recentTeammates == null) {
            return Collections.emptyList();
        }
        return recentTeammates.stream()
                .map(TeammateDTO::getSteamId)
                .toList();
    }

    /**
     * Saves player data to the database.
     * Converts DTO to entity and persists it.
     *
     * @param dto The PlayerDataDTO to save
     */
    private void savePlayerData(PlayerDataDTO dto) {
        try {
            PlayerData entity = playerDataMapper.toEntity(dto);
            if (entity != null) {
                playerDataRepository.save(entity);
                log.debug("Saved player data to database for Steam ID: {} ({})", dto.getSteamId(), dto.getName());
            }
        } catch (Exception e) {
            log.error("Failed to save player data for Steam ID: {} - {}", dto.getSteamId(), e.getMessage(), e);
            // Don't rethrow - we don't want database failures to break the API fetch
        }
    }
}
