package com.joey.cheaterbuster.dto.leetify.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BanEntryDTO {
    @JsonProperty("platform")
    private String platform;

    @JsonProperty("banned_since")
    private String bannedDate;
}
