package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.dto.model.PredictionResultDTO;
import com.joey.cheaterbuster.service.ModelService;
import com.joey.cheaterbuster.service.player.LeetifyPlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/model")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;
    private final LeetifyPlayerService leetifyPlayerService;

    @PostMapping("/predict")
    public ResponseEntity<PredictionResultDTO> predict(
            @RequestParam(name = "steamId") String steamId
    ) {
        log.info("POST /api/model/predict - Received prediction request for Steam ID: {}", steamId);
        PlayerDataDTO playerData = leetifyPlayerService.getPlayerProfile(steamId);
        PredictionResultDTO result = modelService.getPrediction(playerData);
        return ResponseEntity.ok(result);
    }
}
