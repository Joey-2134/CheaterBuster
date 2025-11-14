package com.joey.cheaterbuster.dto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PredictionResultDTO {
    private int prediction;
    private double probability;
    private double confidence;

    @JsonProperty("risk_level")
    private String riskLevel;
}
