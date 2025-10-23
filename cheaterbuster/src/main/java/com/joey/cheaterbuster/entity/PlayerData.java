package com.joey.cheaterbuster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {

    @Id
    @Column(name = "steam_id", nullable = false, length = 20)
    private String steamId;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "win_rate")
    private Double winRate;

    @Column(name = "total_matches")
    private Integer totalMatches;

    // Ranks
    @Column(name = "rank_premier")
    private Integer rankPremier;

    @Column(name = "rank_faceit_elo")
    private Integer rankFaceitElo;

    // Ratings
    @Column(name = "rating_aim")
    private Double ratingAim;

    @Column(name = "rating_positioning")
    private Double ratingPositioning;

    @Column(name = "rating_utility")
    private Double ratingUtility;

    @Column(name = "rating_clutch")
    private Double ratingClutch;

    @Column(name = "rating_opening")
    private Double ratingOpening;

    @Column(name = "rating_ct_leetify")
    private Double ratingCtLeetify;

    @Column(name = "rating_t_leetify")
    private Double ratingTLeetify;

    // Key Stats
    @Column(name = "accuracy_head")
    private Double accuracyHead;

    @Column(name = "accuracy_enemy_spotted")
    private Double accuracyEnemySpotted;

    @Column(name = "reaction_time_ms")
    private Double reactionTimeMs;

    @Column(name = "spray_accuracy")
    private Double sprayAccuracy;

    @Column(name = "preaim")
    private Double preaim;

    @Column(name = "counter_strafing_good_shots_ratio")
    private Double counterStrafingGoodShotsRatio;

    // Opening Duels
    @Column(name = "ct_opening_duel_success_percentage")
    private Double ctOpeningDuelSuccessPercentage;

    @Column(name = "t_opening_duel_success_percentage")
    private Double tOpeningDuelSuccessPercentage;

    @Column(name = "ct_opening_aggression_success_rate")
    private Double ctOpeningAggressionSuccessRate;

    @Column(name = "t_opening_aggression_success_rate")
    private Double tOpeningAggressionSuccessRate;

    // Utility Stats
    @Column(name = "flashbang_thrown")
    private Double flashbangThrown;

    @Column(name = "flashbang_hit_foe_per_flashbang")
    private Double flashbangHitFoePerFlashbang;

    @Column(name = "flashbang_hit_friend_per_flashbang")
    private Double flashbangHitFriendPerFlashbang;

    @Column(name = "flashbang_hit_foe_avg_duration")
    private Double flashbangHitFoeAvgDuration;

    @Column(name = "flashbang_leading_to_kill")
    private Double flashbangLeadingToKill;

    @Column(name = "he_foes_damage_avg")
    private Double heFoesDamageAvg;

    @Column(name = "he_friends_damage_avg")
    private Double heFriendsDamageAvg;

    @Column(name = "utility_on_death_avg")
    private Double utilityOnDeathAvg;

    // Trade Stats
    @Column(name = "trade_kills_success_percentage")
    private Double tradeKillsSuccessPercentage;

    @Column(name = "traded_deaths_success_percentage")
    private Double tradedDeathsSuccessPercentage;

    @Column(name = "trade_kill_opportunities_per_round")
    private Double tradeKillOpportunitiesPerRound;

    // Bans relationship
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ban> bans = new ArrayList<>();

    // Audit timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}