package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.service.match.LeetifyMatchService;
import com.joey.cheaterbuster.service.player.LeetifyPlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/leetify")
@RequiredArgsConstructor
public class LeetifyController {
    private final LeetifyPlayerService leetifyPlayerService;
    private final LeetifyMatchService leetifyMatchService;

    @GetMapping("/player/{steamId}")
    public PlayerDataDTO getPlayerData(@PathVariable String steamId) {
        log.info("Getting player profile for steamId: {}", steamId);
        return leetifyPlayerService.getPlayerProfile(steamId);
    }

    @GetMapping("/player/{numEntries}/{steamId}")
    public List<PlayerDataDTO> getPlayerProfiles(@PathVariable int numEntries, @PathVariable String steamId) {
        log.info("Getting {} player profiles starting from steamId: {}", numEntries, steamId);
        return leetifyPlayerService.getPlayerProfiles(numEntries, steamId);
    }

    @GetMapping("/player/matches/{steamId}")
    public List<MatchDTO> getPlayerMatchHistory(@PathVariable String steamId) {
        log.info("Getting match history for steamId: {}", steamId);
        return leetifyMatchService.getMatchHistory(steamId);
    }

    @GetMapping("/match/{gameId}")
    public MatchDTO getMatchDetails(@PathVariable String gameId) {
        log.info("Getting match details for gameId: {}", gameId);
        return leetifyMatchService.getMatchDetails(gameId);
    }
}
