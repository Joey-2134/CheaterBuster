package com.joey.cheaterbuster.dto.leetify.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RanksDTO {
    @JsonProperty("premier")
    private int premier;

    @JsonProperty("faceit_elo")
    private int faceit;
}
