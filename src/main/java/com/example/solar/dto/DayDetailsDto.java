package com.example.solar.dto;

import com.example.solar.SourceNames;

import java.util.List;
import java.util.Map;

public record DayDetailsDto(Double dayAccuracy, SourceNames bestSource, Double averageError, Double maxDeviation,
                            Map<SourceNames, List<Double>> hourlyErrors) {
}
