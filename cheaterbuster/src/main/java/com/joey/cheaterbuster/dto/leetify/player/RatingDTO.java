package com.joey.cheaterbuster.dto.leetify.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RatingDTO {
    private double aim;
    private double positioning;
    private double utility;
    private double clutch;
    private double opening;

    @JsonProperty("ct_leetify")
    private double ctLeetify;

    @JsonProperty("t_leetify")
    private double tLeetify;
}
