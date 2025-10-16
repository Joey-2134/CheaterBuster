package com.joey.cheaterbuster.dto.leetify.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatsDTO {
    @JsonProperty("steam64_id")
    private String steam64Id;

    private String name;
}
