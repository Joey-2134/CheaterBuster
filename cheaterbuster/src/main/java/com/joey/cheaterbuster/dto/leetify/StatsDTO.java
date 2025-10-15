package com.joey.cheaterbuster.dto.leetify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatsDTO {
    @JsonProperty("accuracy_enemy_spotted")
    private double accuracyEnemySpotted;

    @JsonProperty("accuracy_head")
    private double accuracyHead;

    @JsonProperty("counter_strafing_good_shots_ratio")
    private double counterStrafingGoodShotsRatio;

    @JsonProperty("ct_opening_aggression_success_rate")
    private double ctOpeningAggressionSuccessRate;

    @JsonProperty("ct_opening_duel_success_percentage")
    private double ctOpeningDuelSuccessPercentage;

    @JsonProperty("flashbang_hit_foe_avg_duration")
    private double flashbangHitFoeAvgDuration;

    @JsonProperty("flashbang_hit_foe_per_flashbang")
    private double flashbangHitFoePerFlashbang;

    @JsonProperty("flashbang_hit_friend_per_flashbang")
    private double flashbangHitFriendPerFlashbang;

    @JsonProperty("flashbang_leading_to_kill")
    private double flashbangLeadingToKill;

    @JsonProperty("flashbang_thrown")
    private double flashbangThrown;

    @JsonProperty("he_foes_damage_avg")
    private double heFoesDamageAvg;

    @JsonProperty("he_friends_damage_avg")
    private double heFriendsDamageAvg;

    @JsonProperty("preaim")
    private double preaim;

    @JsonProperty("reaction_time_ms")
    private double reactionTimeMs;

    @JsonProperty("spray_accuracy")
    private double sprayAccuracy;

    @JsonProperty("t_opening_aggression_success_rate")
    private double tOpeningAggressionSuccessRate;

    @JsonProperty("t_opening_duel_success_percentage")
    private double tOpeningDuelSuccessPercentage;

    @JsonProperty("traded_deaths_success_percentage")
    private double tradedDeathsSuccessPercentage;

    @JsonProperty("trade_kill_opportunities_per_round")
    private double tradeKillOpportunitiesPerRound;

    @JsonProperty("trade_kills_success_percentage")
    private double tradeKillsSuccessPercentage;

    @JsonProperty("utility_on_death_avg")
    private double utilityOnDeathAvg;
}
