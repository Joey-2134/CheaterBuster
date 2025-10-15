package com.joey.cheaterbuster.service;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.leetify.PlayerDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LeetifyService {

    private final RestTemplate restTemplate;
    private final LeetifyConfig config;

    private final String GET_PROFILE_PATH = "/v3/profile?steam64_id=";

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
}
