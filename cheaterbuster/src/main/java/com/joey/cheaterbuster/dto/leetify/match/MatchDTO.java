package com.joey.cheaterbuster.dto.leetify.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MatchDTO {
    @JsonProperty("id")
    private String matchId;

    @JsonProperty("stats")
    private List<StatsDTO> stats;
}
