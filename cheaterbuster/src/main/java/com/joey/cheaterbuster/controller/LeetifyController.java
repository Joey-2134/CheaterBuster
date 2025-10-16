package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.dto.leetify.match.MatchDTO;
import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.service.match.LeetifyMatchService;
import com.joey.cheaterbuster.service.player.LeetifyPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leetify")
@RequiredArgsConstructor
public class LeetifyController {
    private final LeetifyPlayerService leetifyPlayerService;
    private final LeetifyMatchService leetifyMatchService;

    @GetMapping("/player/{steamId}")
    public PlayerDataDTO getPlayerData(@PathVariable String steamId) {
        return leetifyPlayerService.getPlayerProfile(steamId);
    }

    @GetMapping("/player/{numEntries}/{steamId}")
    public List<PlayerDataDTO> getPlayerProfiles(@PathVariable int numEntries, @PathVariable String steamId) {
        return leetifyPlayerService.getPlayerProfiles(numEntries, steamId);
    }

    @GetMapping("/player/matches/{steamId}")
    public List<MatchDTO> getPlayerMatchHistory(@PathVariable String steamId) {
        System.out.println("Fetching match history for Steam ID: " + steamId);
        return leetifyMatchService.getMatchHistory(steamId);
    }

    @GetMapping("/match/{gameId}")
    public MatchDTO getMatchDetails(@PathVariable String gameId) {
        System.out.println("Fetching match details for Game ID: " + gameId);
        return leetifyMatchService.getMatchDetails(gameId);
    }
}
