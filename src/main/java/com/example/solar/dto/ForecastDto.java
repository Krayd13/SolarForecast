package com.example.solar.dto;

import com.example.solar.SourceNames;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ForecastDto(
        @NotNull(message = "Назва станції є обов'язковим полем")
        Long stationId,
        @NotNull(message = "Дата та час прогнозу є обов'язковим полем")
        LocalDateTime timestamp,
        @NotNull(message = "Назва джерела є обов'язковим полем")
        SourceNames sourceName,
        @NotNull(message = "Значення прогнозу є обов'язковим полем")
        Double value) {
}
