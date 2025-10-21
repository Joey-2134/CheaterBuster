package com.joey.cheaterbuster.service.player;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import com.joey.cheaterbuster.dto.leetify.match.StatsDTO;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.dto.leetify.player.TeammateDTO;
import com.joey.cheaterbuster.service.match.LeetifyMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LeetifyPlayerService {

    private static final int MAX_PLAYER_IDS_FROM_MATCHES = 20;
    private static final String GET_PROFILE_PATH = "/v3/profile?steam64_id=";
    private final RestTemplate restTemplate;
    private final LeetifyConfig config;
    private final LeetifyMatchService leetifyMatchService;

    /**
     * Fetches the profile of a player given their Steam64 ID.
     *
     * @param steam64Id The Steam64 ID of the player
     * @return PlayerDataDTO containing the player's profile information
     */
    public PlayerDataDTO getPlayerProfile(String steam64Id) {
        String url = config.getBaseUrl() + GET_PROFILE_PATH + steam64Id;

        HttpHeaders headers = new HttpHeaders();
        if (!config.getApiKey().isEmpty()) {
            headers.set("_leetify_key", config.getApiKey());
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PlayerDataDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                PlayerDataDTO.class
        );

        return response.getBody();
    }

    /**
     * Fetches player profiles starting from an initial player and traversing through their teammates
     * until the specified number of entries is reached. If player has no recent teamemates and Ids to check runs out,
     * it will look at recent matches to find more players.
     *
     * @param numEntries The target number of profiles to fetch
     * @param firstId The Steam64 ID of the initial player
     * @return List of player profiles
     */

    public List<PlayerDataDTO> getPlayerProfiles(int numEntries, String firstId) {
        Set<String> checkedIds = new HashSet<>();
        Set<String> enqueuedIds = new HashSet<>();
        List<PlayerDataDTO> profiles = new ArrayList<>();
        Queue<String> idsToCheck = new LinkedList<>();

        idsToCheck.add(firstId);
        enqueuedIds.add(firstId);

        while (!idsToCheck.isEmpty() && profiles.size() < numEntries) {
            String currentId = idsToCheck.poll();

            PlayerDataDTO profile;
            try {
                profile = getPlayerProfile(currentId);
            } catch (Exception e) {
                // mark as checked to avoid retrying
                checkedIds.add(currentId);
                continue;
            }

            // If profile couldn't be retrieved, mark checked and continue
            if (profile == null) {
                checkedIds.add(currentId);
                continue;
            }

            // Successfully retrieved
            profiles.add(profile);
            checkedIds.add(currentId);

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

            // If no teammates were added, try expanding via recent matches
            if (!anyTeammatesAdded) {
                Set<String> matchPlayerIds = getPlayerIdsFromRecentMatches(profile);
                for (String id : matchPlayerIds) {
                    if (!checkedIds.contains(id) && enqueuedIds.add(id)) {
                        idsToCheck.add(id);
                    }
                }
            }
        }

        return profiles;
    }

    /**
     * Utility method to extract Steam64 IDs from players in recent matches.
     * Stops fetching match details once MAX_PLAYER_IDS_FROM_MATCHES is reached to reduce API calls.
     * @param profile The PlayerDataDTO whose recent matches are to be analyzed
     * @return Set of Steam64 IDs from recent matches
     */
    private Set<String> getPlayerIdsFromRecentMatches(PlayerDataDTO profile) {
        List<MatchDTO> recentMatches;
        Set<String> playerIds = new HashSet<>();

        recentMatches = leetifyMatchService.getMatchHistory(profile.getSteamId());

        for (MatchDTO match : recentMatches) {
            // Stop fetching match details if we've collected enough player IDs
            if (playerIds.size() >= MAX_PLAYER_IDS_FROM_MATCHES) {
                break;
            }

            MatchDTO matchDetails = leetifyMatchService.getMatchDetails(match.getMatchId());
            if (matchDetails != null && matchDetails.getStats() != null) {
                for (StatsDTO stats : matchDetails.getStats()) {
                    playerIds.add(stats.getSteam64Id());

                    // Check again after adding each player ID
                    if (playerIds.size() >= MAX_PLAYER_IDS_FROM_MATCHES) {
                        break;
                    }
                }
            }
        }

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
}
