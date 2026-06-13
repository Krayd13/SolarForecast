package com.example.solar.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoDto(@JsonProperty("latitude") Double latitude,
                           @JsonProperty("longitude") Double longitude,
                           @JsonProperty("hourly") HourlyData hourlyData) {
    public record HourlyData(
            @JsonProperty("time") List<String> time,
            @JsonProperty("shortwave_radiation") List<Double> shortwaveRadiation){

    }
}
