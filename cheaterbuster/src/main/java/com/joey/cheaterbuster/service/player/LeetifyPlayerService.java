package com.joey.cheaterbuster.service.player;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.dto.leetify.player.TeammateDTO;
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

    private final RestTemplate restTemplate;
    private final LeetifyConfig config;

    /**
     * Fetches the profile of a player given their Steam64 ID.
     *
     * @param steam64Id The Steam64 ID of the player
     * @return PlayerDataDTO containing the player's profile information
     */
    public PlayerDataDTO getPlayerProfile(String steam64Id) {
        String GET_PROFILE_PATH = "/v3/profile?steam64_id=";
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
     * until the specified number of entries is reached.
     *
     * @param numEntries The target number of profiles to fetch
     * @param firstId The Steam64 ID of the initial player
     * @return List of player profiles
     */
    public List<PlayerDataDTO> getPlayerProfiles(int numEntries, String firstId) {
        Set<String> checkedIds = new HashSet<>();
        List<PlayerDataDTO> profiles = new ArrayList<>();
        Queue<String> idsToCheck = new LinkedList<>();

        idsToCheck.add(firstId);

        while (!idsToCheck.isEmpty() && profiles.size() < numEntries) {
            String currentId = idsToCheck.poll();

            // Skip if already checked
            if (checkedIds.contains(currentId)) {
                continue;
            }

            try {
                // Fetch the profile
                PlayerDataDTO profile = getPlayerProfile(currentId);

                if (profile != null) {
                    profiles.add(profile);
                    checkedIds.add(currentId);

                    // Add teammates to queue if we need more profiles
                    if (profiles.size() < numEntries) {
                        List<String> teammateIds = getRecentTeammateIds(profile);
                        for (String teammateId : teammateIds) {
                            if (!checkedIds.contains(teammateId)) {
                                idsToCheck.add(teammateId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                checkedIds.add(currentId); // Mark as checked to avoid retrying
            }
        }
        return profiles;
    }


    /**
     * Utility method to extract Steam64 IDs from a player's recent teammates.
     * @param playerData The PlayerDataDTO containing recent teammates
     * @return List of Steam64 IDs
     */
    private List<String> getRecentTeammateIds(PlayerDataDTO playerData) {
        return playerData.getRecentTeammates().stream()
                .map(TeammateDTO::getSteamId)
                .toList();
    }
}
