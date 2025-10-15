package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.dto.leetify.PlayerDataDTO;
import com.joey.cheaterbuster.service.LeetifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leetify")
@RequiredArgsConstructor
public class LeetifyController {
    private final LeetifyService leetifyService;

    @GetMapping("/player/{steamId}")
    public PlayerDataDTO getPlayerData(@PathVariable String steamId) {
        return leetifyService.getPlayerProfile(steamId);
    }
}
