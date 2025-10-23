package com.joey.cheaterbuster.mapper;

import com.joey.cheaterbuster.dto.leetify.player.*;
import com.joey.cheaterbuster.entity.PlayerData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * Maps a PlayerData entity from database to a PlayerDataDTO.
     *
     * @param entity The PlayerData entity from database
     * @return PlayerDataDTO for API response
     */
    public PlayerDataDTO toDTO(PlayerData entity) {
        if (entity == null) {
            log.warn("Attempted to map null PlayerData entity to DTO");
            return null;
        }

        log.debug("Mapping PlayerData entity to DTO for Steam ID: {}", entity.getSteamId());

        PlayerDataDTO dto = new PlayerDataDTO();
        dto.setSteamId(entity.getSteamId());
        dto.setName(entity.getName());
        dto.setWinRate(entity.getWinRate());
        dto.setTotalMatches(entity.getTotalMatches());

        // Map ranks
        RanksDTO ranks = new RanksDTO();
        ranks.setPremier(entity.getRankPremier());
        ranks.setFaceit(entity.getRankFaceitElo());
        dto.setRanks(ranks);

        // Map ratings
        RatingDTO ratings = new RatingDTO();
        ratings.setAim(entity.getRatingAim());
        ratings.setPositioning(entity.getRatingPositioning());
        ratings.setUtility(entity.getRatingUtility());
        ratings.setClutch(entity.getRatingClutch());
        ratings.setOpening(entity.getRatingOpening());
        ratings.setCtLeetify(entity.getRatingCtLeetify());
        ratings.setTLeetify(entity.getRatingTLeetify());
        dto.setRatings(ratings);

        // Map stats
        StatsDTO stats = new StatsDTO();
        stats.setAccuracyHead(entity.getAccuracyHead());
        stats.setAccuracyEnemySpotted(entity.getAccuracyEnemySpotted());
        stats.setReactionTimeMs(entity.getReactionTimeMs());
        stats.setSprayAccuracy(entity.getSprayAccuracy());
        stats.setPreaim(entity.getPreaim());
        stats.setCounterStrafingGoodShotsRatio(entity.getCounterStrafingGoodShotsRatio());
        stats.setCtOpeningDuelSuccessPercentage(entity.getCtOpeningDuelSuccessPercentage());
        stats.setTOpeningDuelSuccessPercentage(entity.getTOpeningDuelSuccessPercentage());
        stats.setCtOpeningAggressionSuccessRate(entity.getCtOpeningAggressionSuccessRate());
        stats.setTOpeningAggressionSuccessRate(entity.getTOpeningAggressionSuccessRate());
        stats.setFlashbangThrown(entity.getFlashbangThrown());
        stats.setFlashbangHitFoePerFlashbang(entity.getFlashbangHitFoePerFlashbang());
        stats.setFlashbangHitFriendPerFlashbang(entity.getFlashbangHitFriendPerFlashbang());
        stats.setFlashbangHitFoeAvgDuration(entity.getFlashbangHitFoeAvgDuration());
        stats.setFlashbangLeadingToKill(entity.getFlashbangLeadingToKill());
        stats.setHeFoesDamageAvg(entity.getHeFoesDamageAvg());
        stats.setHeFriendsDamageAvg(entity.getHeFriendsDamageAvg());
        stats.setUtilityOnDeathAvg(entity.getUtilityOnDeathAvg());
        stats.setTradeKillsSuccessPercentage(entity.getTradeKillsSuccessPercentage());
        stats.setTradedDeathsSuccessPercentage(entity.getTradedDeathsSuccessPercentage());
        stats.setTradeKillOpportunitiesPerRound(entity.getTradeKillOpportunitiesPerRound());
        dto.setStats(stats);

        // Note: We don't store bans or recentTeammates in the database, so these will be empty/null
        dto.setBans(Collections.emptyList());
        dto.setRecentTeammates(Collections.emptyList());

        log.debug("Successfully mapped DTO for Steam ID: {} ({})", entity.getSteamId(), entity.getName());
        return dto;
    }
}