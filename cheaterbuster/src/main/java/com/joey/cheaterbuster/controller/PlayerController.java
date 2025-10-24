package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.service.match.LeetifyMatchService;
import com.joey.cheaterbuster.service.player.LeetifyPlayerService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayerController {
    private final LeetifyPlayerService leetifyPlayerService;
    private final LeetifyMatchService leetifyMatchService;

    /**
     * Get a single player's profile by Steam ID
     * GET /api/players/{steamId}
     */
    @GetMapping("/players/{steamId}")
    public PlayerDataDTO getPlayer(
            @PathVariable @NotBlank(message = "Steam ID cannot be blank") String steamId) {
        log.info("GET /api/players/{} - Fetching player profile", steamId);
        return leetifyPlayerService.getPlayerProfile(steamId);
    }

    /**
     * Get a network of players starting from a seed player
     * GET /api/players/{steamId}/network?limit=10
     */
    @GetMapping("/players/{steamId}/network")
    public List<PlayerDataDTO> getPlayerNetwork(
            @PathVariable @NotBlank(message = "Steam ID cannot be blank") String steamId,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Limit must be at least 1") @Max(value = 100, message = "Limit cannot exceed 100") int limit) {
        log.info("GET /api/players/{}/network?limit={} - Fetching player network", steamId, limit);
        return leetifyPlayerService.getPlayerProfiles(limit, steamId);
    }

    /**
     * Get a list of banned players
     *
     * @param limit num of banned profiles to fetch
     * @return List of banned players
     */
    @GetMapping("/players/banned")
    public List<PlayerDataDTO> getBannedPlayers(
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Limit must be at least 1") @Max(value = 100, message = "Limit cannot exceed 100") int limit) {
        log.info("GET /api/players/banned?limit={} - Fetching banned players", limit);
        return leetifyPlayerService.getBannedPlayerProfiles(limit);
    }

    /**
     * Get match history for a player
     * GET /api/players/{steamId}/matches
     */
    @GetMapping("/players/{steamId}/matches")
    public List<MatchDTO> getPlayerMatches(
            @PathVariable @NotBlank(message = "Steam ID cannot be blank") String steamId) {
        log.info("GET /api/players/{}/matches - Fetching match history", steamId);
        return leetifyMatchService.getMatchHistory(steamId);
    }

    /**
     * Get detailed match information by game ID
     * GET /api/matches/{gameId}
     */
    @GetMapping("/matches/{gameId}")
    public MatchDTO getMatch(
            @PathVariable @NotBlank(message = "Game ID cannot be blank") String gameId) {
        log.info("GET /api/matches/{} - Fetching match details", gameId);
        return leetifyMatchService.getMatchDetails(gameId);
    }
}