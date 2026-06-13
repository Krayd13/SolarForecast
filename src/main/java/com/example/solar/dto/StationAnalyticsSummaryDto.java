package com.example.solar.dto;

import java.util.List;

public record StationAnalyticsSummaryDto(Double hitPercent, Double mape, Double rmse, Integer completedForecasts, List<DailyAccuracyDto> sourcesAccuracy) {
}
