package com.joey.cheaterbuster.dto.leetify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VaclistProfileDTO {
    private Long id;

    @JsonProperty("steam_id")
    private String steamId;

    @JsonProperty("profile_url")
    private String profileUrl;

    private String personaname;

    private String avatar;

    private String timecreated;

    private Integer communityvisibilitystate;

    @JsonProperty("vac_bans")
    private Integer vacBans;

    @JsonProperty("game_bans")
    private Integer gameBans;

    @JsonProperty("economy_ban")
    private String economyBan;

    @JsonProperty("last_ban")
    private Integer lastBan;

    @JsonProperty("last_checked")
    private String lastChecked;

    @JsonProperty("tracked_by")
    private Integer trackedBy;
}
