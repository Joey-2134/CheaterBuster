package com.joey.cheaterbuster.service;

import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.entity.PlayerData;
import com.joey.cheaterbuster.repository.PlayerDataRepository;
import com.joey.cheaterbuster.service.player.LeetifyPlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataGatheringService {

    private final LeetifyPlayerService leetifyPlayerService;
    private final PlayerDataRepository playerDataRepository;

    @Value("${gathering.delay-between-batches:5000}")
    private long delayBetweenBatches;

    @Value("${gathering.error-retry-delay:30000}")
    private long errorRetryDelay;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicBoolean shouldStop = new AtomicBoolean(false);
    private final AtomicInteger totalGathered = new AtomicInteger(0);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Instant startTime;
    private GatheringMode currentMode = GatheringMode.BANNED;
    private int batchSize;

    public enum GatheringMode {
        BANNED,
        RANDOM
    }

    /**
     * Start the data gathering process in the background
     */
    public synchronized boolean start(GatheringMode mode, int batchSize) {
        if (isRunning.get()) {
            log.warn("Data gathering is already running");
            return false;
        }

        this.currentMode = mode;
        this.batchSize = batchSize;
        this.shouldStop.set(false);
        this.totalGathered.set(0);
        this.startTime = Instant.now();

        isRunning.set(true);

        executor.submit(() -> {
            try {
                runGatheringLoop();
            } catch (Exception e) {
                log.error("Data gathering failed with exception", e);
            } finally {
                isRunning.set(false);
                log.info("Data gathering stopped");
            }
        });

        log.info("Data gathering started - Mode: {}, Batch size: {}", mode, batchSize);
        return true;
    }

    /**
     * Stop the data gathering process
     */
    public synchronized boolean stop() {
        if (!isRunning.get()) {
            log.warn("Data gathering is not running");
            return false;
        }

        log.info("Stopping data gathering...");
        shouldStop.set(true);
        return true;
    }

    /**
     * Main gathering loop that runs continuously
     */
    private void runGatheringLoop() {
        log.info("Starting gathering loop - Mode: {}, Batch size: {}", currentMode, batchSize);

        while (!shouldStop.get()) {
            try {
                int gathered = switch (currentMode) {
                    case BANNED -> gatherBannedPlayers();
                    case RANDOM -> gatherRandomPlayers();
                };

                totalGathered.addAndGet(gathered);
                log.info("Batch complete - Gathered: {} profiles, Total: {}", gathered, totalGathered.get());

                // Small delay between batches to avoid overwhelming APIs
                if (!shouldStop.get()) {
                    Thread.sleep(delayBetweenBatches);
                }

            } catch (InterruptedException e) {
                log.info("Gathering loop interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in gathering loop", e);
                // Continue despite errors - don't want one error to stop everything
                try {
                    Thread.sleep(errorRetryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("Gathering loop finished - Total profiles gathered: {}", totalGathered.get());
    }

    /**
     * Gather a batch of banned players
     */
    private int gatherBannedPlayers() {
        log.debug("Fetching batch of {} banned players", batchSize);

        try {
            List<PlayerDataDTO> profiles = leetifyPlayerService.getBannedPlayerProfiles(batchSize);
            log.info("Successfully gathered {} banned player profiles", profiles.size());
            return profiles.size();
        } catch (Exception e) {
            log.error("Failed to gather banned players", e);
            return 0;
        }
    }

    /**
     * Gather a batch of random players
     */
    private int gatherRandomPlayers() {
        log.debug("Fetching batch of {} players via random network traversal", batchSize);

        try {
            // Get a random player from database as seed
            Optional<PlayerData> randomPlayer = playerDataRepository.findRandomPlayer();

            if (randomPlayer.isEmpty()) {
                log.warn("No players in database to use as seed for random gathering. Please add some players first.");
                return 0;
            }

            String seedSteamId = randomPlayer.get().getSteamId();
            log.info("Using player {} as seed for network traversal", seedSteamId);

            List<PlayerDataDTO> profiles = leetifyPlayerService.getPlayerProfiles(batchSize, seedSteamId);
            log.info("Successfully gathered {} player profiles via network traversal", profiles.size());
            return profiles.size();

        } catch (Exception e) {
            log.error("Failed to gather random players", e);
            return 0;
        }
    }

    /**
     * Get current status of data gathering
     */
    public GatheringStatus getStatus() {
        return GatheringStatus.builder()
                .isRunning(isRunning.get())
                .mode(currentMode)
                .batchSize(batchSize)
                .totalProfilesGathered(totalGathered.get())
                .uptime(isRunning.get() && startTime != null ?
                        Duration.between(startTime, Instant.now()).getSeconds() : 0)
                .build();
    }

    /**
     * Status DTO for API responses
     */
    @lombok.Builder
    @lombok.Data
    public static class GatheringStatus {
        private boolean isRunning;
        private GatheringMode mode;
        private int batchSize;
        private int totalProfilesGathered;
        private long uptime; // in seconds
    }
}