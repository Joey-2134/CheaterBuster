package com.joey.cheaterbuster.controller;

import com.joey.cheaterbuster.service.DataGatheringService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/gathering")
@RequiredArgsConstructor
public class GatheringController {
    private final DataGatheringService dataGatheringService;

    /**
     * Start data gathering
     *
     * @param mode gathering mode (BANNED or RANDOM)
     * @param batchSize number of profiles to gather per batch
     * @return status of the operation
     */
    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startGathering(
            @RequestParam(defaultValue = "RANDOM") String mode,
            @RequestParam(defaultValue = "50") @Min(value = 1, message = "Batch size must be at least 1") @Max(value = 50, message = "Batch size cannot exceed 50") int batchSize) {
        log.info("POST /api/gathering/start - Mode: {}, Batch size: {}", mode, batchSize);

        try {
            DataGatheringService.GatheringMode gatheringMode = DataGatheringService.GatheringMode.valueOf(mode);
            boolean started = dataGatheringService.start(gatheringMode, batchSize);

            if (started) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Data gathering started successfully",
                        "mode", mode,
                        "batchSize", batchSize
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Data gathering is already running"
                ));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid gathering mode. Use BANNED or RANDOM"
            ));
        }
    }

    /**
     * Stop data gathering
     *
     * @return status of the operation
     */
    @PostMapping("/stop")
    public ResponseEntity<Map<String, Object>> stopGathering() {
        log.info("POST /api/gathering/stop - Stopping data gathering");

        boolean stopped = dataGatheringService.stop();
        if (stopped) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Data gathering stop signal sent"
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Data gathering is not running"
            ));
        }
    }

    /**
     * Get current status of data gathering
     *
     * @return current status
     */
    @GetMapping("/status")
    public ResponseEntity<DataGatheringService.GatheringStatus> getGatheringStatus() {
        log.info("Fetching gathering status");
        return ResponseEntity.ok(dataGatheringService.getStatus());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount() {
        log.info("Fetching gathering count");
        return ResponseEntity.ok(dataGatheringService.getCount());
    }
}