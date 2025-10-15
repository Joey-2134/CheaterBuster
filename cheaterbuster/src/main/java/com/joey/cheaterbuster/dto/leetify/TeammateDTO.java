package com.joey.cheaterbuster.dto.leetify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TeammateDTO {
    @JsonProperty("steam64_id")
    private String steamId;
}
