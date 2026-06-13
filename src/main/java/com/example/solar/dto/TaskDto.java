package com.example.solar.dto;

import com.example.solar.SourceNames;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskDto(
        @NotNull(message = "Станція є обов'язкова для прив'язки завдання")
        Long stationId,
        @NotNull(message = "Назва джерела є обов'язковим полем")
        SourceNames sourceName,
        @NotNull(message = "Дата початку завдання є обов'язковим полем")
        LocalDate startDate,
        @NotNull(message = "Дата закінчення завдання є обов'язковим полем")
        LocalDate endDate,
        @NotNull(message = "Статус активності завдання є обов'язковим")
        Boolean isActive) {
}
