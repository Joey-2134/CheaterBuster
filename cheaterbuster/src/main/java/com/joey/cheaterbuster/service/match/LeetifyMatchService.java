package com.joey.cheaterbuster.service.match;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeetifyMatchService {
    private final RestTemplate restTemplate;
    private final LeetifyConfig config;

    /**
     * Fetches the match history for a player given their Steam64 ID.
     *
     * @param steamId The Steam64 ID of the player
     * @return List of MatchDTO containing the player's match history
     */
    public List<MatchDTO> getMatchHistory(String steamId) {
        String GET_MATCH_HISTORY_PATH = "/v3/profile/matches?steam64_id=";
        String url = config.getBaseUrl() + GET_MATCH_HISTORY_PATH + steamId;

        HttpHeaders headers = new HttpHeaders();
        if (!config.getApiKey().isEmpty()) {
            headers.set("_leetify_key", config.getApiKey());
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MatchDTO[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MatchDTO[].class
        );

        MatchDTO[] body = response.getBody();
        return body == null ? Collections.emptyList() : Arrays.asList(body);
    }

    public MatchDTO getMatchDetails(String gameId) {
        String GET_MATCH_DETAILS_PATH = "/v2/matches/";
        String url = config.getBaseUrl() + GET_MATCH_DETAILS_PATH + gameId;

        HttpHeaders headers = new HttpHeaders();
        if (!config.getApiKey().isEmpty()) {
            headers.set("_leetify_key", config.getApiKey());
        }

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<MatchDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                MatchDTO.class
        );

        return response.getBody();
    }

}
