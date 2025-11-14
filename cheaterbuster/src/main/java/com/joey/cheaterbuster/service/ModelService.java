package com.joey.cheaterbuster.service;

import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.dto.model.PredictionRequestDTO;
import com.joey.cheaterbuster.dto.model.PredictionResultDTO;
import com.joey.cheaterbuster.entity.PlayerData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelService {

    private final RestTemplate restTemplate;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    public PredictionResultDTO getPrediction(PlayerDataDTO playerData) {
        log.debug("Getting prediction for player: {} ({})", playerData.getName(), playerData.getSteamId());

        PredictionRequestDTO request = buildPredictionRequest(playerData);
        return callMLService(request);
    }

    private PredictionRequestDTO buildPredictionRequest(PlayerDataDTO dto) {
        PredictionRequestDTO.PredictionRequestDTOBuilder builder = PredictionRequestDTO.builder()
                .winRate(dto.getWinRate());

        if (dto.getStats() != null) {
            builder
                    .accuracyEnemySpotted(dto.getStats().getAccuracyEnemySpotted())
                    .accuracyHead(dto.getStats().getAccuracyHead())
                    .counterStrafingGoodShotsRatio(dto.getStats().getCounterStrafingGoodShotsRatio())
                    .ctOpeningAggressionSuccessRate(dto.getStats().getCtOpeningAggressionSuccessRate())
                    .ctOpeningDuelSuccessPercentage(dto.getStats().getCtOpeningDuelSuccessPercentage())
                    .flashbangHitFoeAvgDuration(dto.getStats().getFlashbangHitFoeAvgDuration())
                    .flashbangHitFoePerFlashbang(dto.getStats().getFlashbangHitFoePerFlashbang())
                    .flashbangHitFriendPerFlashbang(dto.getStats().getFlashbangHitFriendPerFlashbang())
                    .flashbangLeadingToKill(dto.getStats().getFlashbangLeadingToKill())
                    .flashbangThrown(dto.getStats().getFlashbangThrown())
                    .heFoesDamageAvg(dto.getStats().getHeFoesDamageAvg())
                    .preaim(dto.getStats().getPreaim())
                    .reactionTimeMs(dto.getStats().getReactionTimeMs())
                    .sprayAccuracy(dto.getStats().getSprayAccuracy())
                    .tOpeningAggressionSuccessRate(dto.getStats().getTOpeningAggressionSuccessRate())
                    .tOpeningDuelSuccessPercentage(dto.getStats().getTOpeningDuelSuccessPercentage())
                    .tradeKillOpportunitiesPerRound(dto.getStats().getTradeKillOpportunitiesPerRound())
                    .tradeKillsSuccessPercentage(dto.getStats().getTradeKillsSuccessPercentage())
                    .tradedDeathsSuccessPercentage(dto.getStats().getTradedDeathsSuccessPercentage())
                    .utilityOnDeathAvg(dto.getStats().getUtilityOnDeathAvg());
        }

        if (dto.getRatings() != null) {
            builder
                    .ratingAim(dto.getRatings().getAim())
                    .ratingClutch(dto.getRatings().getClutch())
                    .ratingCtLeetify(dto.getRatings().getCtLeetify())
                    .ratingOpening(dto.getRatings().getOpening())
                    .ratingPositioning(dto.getRatings().getPositioning())
                    .ratingTLeetify(dto.getRatings().getTLeetify())
                    .ratingUtility(dto.getRatings().getUtility());
        }

        return builder.build();
    }

    private PredictionRequestDTO buildPredictionRequest(PlayerData entity) {
        return PredictionRequestDTO.builder()
                .accuracyEnemySpotted(entity.getAccuracyEnemySpotted())
                .accuracyHead(entity.getAccuracyHead())
                .counterStrafingGoodShotsRatio(entity.getCounterStrafingGoodShotsRatio())
                .ctOpeningAggressionSuccessRate(entity.getCtOpeningAggressionSuccessRate())
                .ctOpeningDuelSuccessPercentage(entity.getCtOpeningDuelSuccessPercentage())
                .flashbangHitFoeAvgDuration(entity.getFlashbangHitFoeAvgDuration())
                .flashbangHitFoePerFlashbang(entity.getFlashbangHitFoePerFlashbang())
                .flashbangHitFriendPerFlashbang(entity.getFlashbangHitFriendPerFlashbang())
                .flashbangLeadingToKill(entity.getFlashbangLeadingToKill())
                .flashbangThrown(entity.getFlashbangThrown())
                .heFoesDamageAvg(entity.getHeFoesDamageAvg())
                .preaim(entity.getPreaim())
                .reactionTimeMs(entity.getReactionTimeMs())
                .sprayAccuracy(entity.getSprayAccuracy())
                .tOpeningAggressionSuccessRate(entity.getTOpeningAggressionSuccessRate())
                .tOpeningDuelSuccessPercentage(entity.getTOpeningDuelSuccessPercentage())
                .tradeKillOpportunitiesPerRound(entity.getTradeKillOpportunitiesPerRound())
                .tradeKillsSuccessPercentage(entity.getTradeKillsSuccessPercentage())
                .tradedDeathsSuccessPercentage(entity.getTradedDeathsSuccessPercentage())
                .utilityOnDeathAvg(entity.getUtilityOnDeathAvg())

                .ratingAim(entity.getRatingAim())
                .ratingClutch(entity.getRatingClutch())
                .ratingCtLeetify(entity.getRatingCtLeetify())
                .ratingOpening(entity.getRatingOpening())
                .ratingPositioning(entity.getRatingPositioning())
                .ratingTLeetify(entity.getRatingTLeetify())
                .ratingUtility(entity.getRatingUtility())

                .winRate(entity.getWinRate())
                .build();
    }

    private PredictionResultDTO callMLService(PredictionRequestDTO request) {
        String url = mlServiceUrl + "/predict";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PredictionRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            log.debug("Calling ML service at: {}", url);
            ResponseEntity<PredictionResultDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    PredictionResultDTO.class
            );

            PredictionResultDTO result = response.getBody();
            if (result != null) {
                log.info("ML prediction received - Prediction: {}, Probability: {}, Risk: {}",
                        result.getPrediction(), result.getProbability(), result.getRiskLevel());
                return result;
            } else {
                log.error("Received null response from ML service");
                throw new RuntimeException("ML service returned null response");
            }
        } catch (HttpClientErrorException e) {
            log.error("ML service error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("ML service error: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error calling ML service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call ML service", e);
        }
    }
}

