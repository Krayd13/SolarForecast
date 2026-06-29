package com.example.solar.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ActualDto(@JsonProperty("result") List<Result> result) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(@JsonProperty("datas") List<VariablesData> datas) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record VariablesData(@JsonProperty("data") List<Data> data) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record Data(@JsonProperty("value") Double value, @JsonProperty("time") String time) {
            }
        }
    }
}
