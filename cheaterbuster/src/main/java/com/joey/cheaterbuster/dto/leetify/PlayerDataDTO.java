package com.joey.cheaterbuster.dto.leetify;

import lombok.Data;

@Data
public class PlayerDataDTO {
    private String steamId;
    private String name;
    private double winRate;
    private int totalMatches;
    private BansDTO bans;
    private RanksDTO ranks;
    private RatingDTO ratings;
    private StatsDTO stats;

}
