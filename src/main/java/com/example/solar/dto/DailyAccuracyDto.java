package com.example.solar.dto;

import com.example.solar.SourceNames;

import java.time.LocalDate;

public record DailyAccuracyDto(Long stationId, SourceNames sourceName, LocalDate date, Double mapeError, Double rmseError) {
}
