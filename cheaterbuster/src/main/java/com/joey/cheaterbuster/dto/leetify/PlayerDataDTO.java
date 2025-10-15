package com.joey.cheaterbuster.dto.leetify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PlayerDataDTO {
    @JsonProperty("steam64_id")
    private String steamId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("winrate")
    private double winRate;

    @JsonProperty("total_matches")
    private int totalMatches;

    @JsonProperty("bans")
    private List<BanEntryDTO> bans;

    @JsonProperty("ranks")
    private RanksDTO ranks;

    @JsonProperty("rating")
    private RatingDTO ratings;

    @JsonProperty("stats")
    private StatsDTO stats;
}
