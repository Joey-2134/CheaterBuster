package com.joey.cheaterbuster.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDTO {

    @JsonProperty("accuracy_enemy_spotted")
    private Double accuracyEnemySpotted;

    @JsonProperty("accuracy_head")
    private Double accuracyHead;

    @JsonProperty("counter_strafing_good_shots_ratio")
    private Double counterStrafingGoodShotsRatio;

    @JsonProperty("ct_opening_aggression_success_rate")
    private Double ctOpeningAggressionSuccessRate;

    @JsonProperty("ct_opening_duel_success_percentage")
    private Double ctOpeningDuelSuccessPercentage;

    @JsonProperty("flashbang_hit_foe_avg_duration")
    private Double flashbangHitFoeAvgDuration;

    @JsonProperty("flashbang_hit_foe_per_flashbang")
    private Double flashbangHitFoePerFlashbang;

    @JsonProperty("flashbang_hit_friend_per_flashbang")
    private Double flashbangHitFriendPerFlashbang;

    @JsonProperty("flashbang_leading_to_kill")
    private Double flashbangLeadingToKill;

    @JsonProperty("flashbang_thrown")
    private Double flashbangThrown;

    @JsonProperty("he_foes_damage_avg")
    private Double heFoesDamageAvg;

    @JsonProperty("preaim")
    private Double preaim;

    @JsonProperty("rating_aim")
    private Double ratingAim;

    @JsonProperty("rating_clutch")
    private Double ratingClutch;

    @JsonProperty("rating_ct_leetify")
    private Double ratingCtLeetify;

    @JsonProperty("rating_opening")
    private Double ratingOpening;

    @JsonProperty("rating_positioning")
    private Double ratingPositioning;

    @JsonProperty("rating_t_leetify")
    private Double ratingTLeetify;

    @JsonProperty("rating_utility")
    private Double ratingUtility;

    @JsonProperty("reaction_time_ms")
    private Double reactionTimeMs;

    @JsonProperty("spray_accuracy")
    private Double sprayAccuracy;

    @JsonProperty("t_opening_aggression_success_rate")
    private Double tOpeningAggressionSuccessRate;

    @JsonProperty("t_opening_duel_success_percentage")
    private Double tOpeningDuelSuccessPercentage;

    @JsonProperty("trade_kill_opportunities_per_round")
    private Double tradeKillOpportunitiesPerRound;

    @JsonProperty("trade_kills_success_percentage")
    private Double tradeKillsSuccessPercentage;

    @JsonProperty("traded_deaths_success_percentage")
    private Double tradedDeathsSuccessPercentage;

    @JsonProperty("utility_on_death_avg")
    private Double utilityOnDeathAvg;

    @JsonProperty("win_rate")
    private Double winRate;
}
