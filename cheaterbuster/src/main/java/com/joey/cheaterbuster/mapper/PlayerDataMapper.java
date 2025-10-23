package com.joey.cheaterbuster.mapper;

import com.joey.cheaterbuster.dto.leetify.player.PlayerDataDTO;
import com.joey.cheaterbuster.entity.PlayerData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class PlayerDataMapper {

    /**
     * Maps a PlayerDataDTO from Leetify API to a PlayerData entity for database storage.
     *
     * @param dto The PlayerDataDTO from Leetify API
     * @return PlayerData entity ready to be persisted
     */
    public PlayerData toEntity(PlayerDataDTO dto) {
        if (dto == null) {
            log.warn("Attempted to map null PlayerDataDTO to entity");
            return null;
        }

        log.debug("Mapping PlayerDataDTO to entity for Steam ID: {}", dto.getSteamId());

        PlayerData entity = PlayerData.builder()
                .steamId(dto.getSteamId())
                .name(dto.getName())
                .winRate(dto.getWinRate())
                .totalMatches(dto.getTotalMatches())
                .build();

        // Map ranks
        if (dto.getRanks() != null) {
            entity.setRankPremier(dto.getRanks().getPremier());
            entity.setRankFaceitElo(dto.getRanks().getFaceit());
        }

        // Map ratings
        if (dto.getRatings() != null) {
            entity.setRatingAim(dto.getRatings().getAim());
            entity.setRatingPositioning(dto.getRatings().getPositioning());
            entity.setRatingUtility(dto.getRatings().getUtility());
            entity.setRatingClutch(dto.getRatings().getClutch());
            entity.setRatingOpening(dto.getRatings().getOpening());
            entity.setRatingCtLeetify(dto.getRatings().getCtLeetify());
            entity.setRatingTLeetify(dto.getRatings().getTLeetify());
        }

        // Map stats
        if (dto.getStats() != null) {
            entity.setAccuracyHead(dto.getStats().getAccuracyHead());
            entity.setAccuracyEnemySpotted(dto.getStats().getAccuracyEnemySpotted());
            entity.setReactionTimeMs(dto.getStats().getReactionTimeMs());
            entity.setSprayAccuracy(dto.getStats().getSprayAccuracy());
            entity.setPreaim(dto.getStats().getPreaim());
            entity.setCounterStrafingGoodShotsRatio(dto.getStats().getCounterStrafingGoodShotsRatio());

            entity.setCtOpeningDuelSuccessPercentage(dto.getStats().getCtOpeningDuelSuccessPercentage());
            entity.setTOpeningDuelSuccessPercentage(dto.getStats().getTOpeningDuelSuccessPercentage());
            entity.setCtOpeningAggressionSuccessRate(dto.getStats().getCtOpeningAggressionSuccessRate());
            entity.setTOpeningAggressionSuccessRate(dto.getStats().getTOpeningAggressionSuccessRate());

            entity.setFlashbangThrown(dto.getStats().getFlashbangThrown());
            entity.setFlashbangHitFoePerFlashbang(dto.getStats().getFlashbangHitFoePerFlashbang());
            entity.setFlashbangHitFriendPerFlashbang(dto.getStats().getFlashbangHitFriendPerFlashbang());
            entity.setFlashbangHitFoeAvgDuration(dto.getStats().getFlashbangHitFoeAvgDuration());
            entity.setFlashbangLeadingToKill(dto.getStats().getFlashbangLeadingToKill());
            entity.setHeFoesDamageAvg(dto.getStats().getHeFoesDamageAvg());
            entity.setHeFriendsDamageAvg(dto.getStats().getHeFriendsDamageAvg());
            entity.setUtilityOnDeathAvg(dto.getStats().getUtilityOnDeathAvg());

            entity.setTradeKillsSuccessPercentage(dto.getStats().getTradeKillsSuccessPercentage());
            entity.setTradedDeathsSuccessPercentage(dto.getStats().getTradedDeathsSuccessPercentage());
            entity.setTradeKillOpportunitiesPerRound(dto.getStats().getTradeKillOpportunitiesPerRound());
        }

        // Map bans
        entity.setHasBan(dto.getBans() != null && !dto.getBans().isEmpty());

        log.debug("Successfully mapped entity for Steam ID: {} ({})", dto.getSteamId(), dto.getName());
        return entity;
    }

    /**
     * Maps a list of PlayerDataDTOs to PlayerData entities.
     *
     * @param dtos List of PlayerDataDTOs
     * @return List of PlayerData entities
     */
    public List<PlayerData> toEntities(List<PlayerDataDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new ArrayList<>();
        }

        log.debug("Mapping {} PlayerDataDTOs to entities", dtos.size());
        return dtos.stream()
                .map(this::toEntity)
                .filter(Objects::nonNull)
                .toList();
    }
}