package com.example.solar.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastSolarDto(@JsonProperty("result") Result result) {
    public record Result(@JsonProperty("watt_hours_period") Map<String, Double> wattHoursPeriod){

    }
}
