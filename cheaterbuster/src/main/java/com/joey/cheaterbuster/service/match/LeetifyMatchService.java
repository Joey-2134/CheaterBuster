package com.joey.cheaterbuster.service.match;

import com.joey.cheaterbuster.config.LeetifyConfig;
import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeetifyMatchService {
    private static final String GET_MATCH_DETAILS_PATH = "/v2/matches/";
    private static final String GET_MATCH_HISTORY_PATH = "/v3/profile/matches?steam64_id=";

    private final RestTemplate restTemplate;
    private final LeetifyConfig config;

    /**
     * Fetches the match history for a player given their Steam64 ID.
     * Rate limited to 1 call per second.
     *
     * @param steamId The Steam64 ID of the player
     * @return List of MatchDTO containing the player's match history
     */
    @RateLimiter(name = "leetifyApi")
    public List<MatchDTO> getMatchHistory(String steamId) {
        log.debug("Fetching match history for Steam ID: {}", steamId);
        String url = config.getBaseUrl() + GET_MATCH_HISTORY_PATH + steamId;

        HttpHeaders headers = Utils.createLeetifyHeaders(config.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MatchDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MatchDTO[].class
            );

            MatchDTO[] body = response.getBody();
            List<MatchDTO> matches = body == null ? Collections.emptyList() : Arrays.asList(body);
            log.info("Successfully fetched {} matches for Steam ID: {}", matches.size(), steamId);
            return matches;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Match history not found for Steam ID: {} (404)", steamId);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching match history for Steam ID: {}", steamId, e);
            throw e;
        }
    }

    /**
     * Fetches detailed match information by game ID.*
     * @param gameId The game ID of the match
     * @return MatchDTO containing detailed match information
     */
    @RateLimiter(name = "leetifyApi")
    public MatchDTO getMatchDetails(String gameId) {
        log.debug("Fetching match details for Game ID: {}", gameId);
        String url = config.getBaseUrl() + GET_MATCH_DETAILS_PATH + gameId;

        HttpHeaders headers = Utils.createLeetifyHeaders(config.getApiKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MatchDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MatchDTO.class
            );

            MatchDTO matchDetails = response.getBody();
            if (matchDetails != null) {
                int playerCount = matchDetails.getStats() != null ? matchDetails.getStats().size() : 0;
                log.info("Successfully fetched match details for Game ID: {} ({} players)", gameId, playerCount);
                return matchDetails;
            } else {
                log.error("Received null response body from Leetify API for Game ID: {}", gameId);
                throw new IllegalStateException("Received null response from Leetify API for Game ID: " + gameId);
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Match details not found for Game ID: {} (404)", gameId);
            return null;
        } catch (Exception e) {
            log.error("Error fetching match details for Game ID: {}", gameId, e);
            throw e;
        }
    }

}
