package com.example.solar.dto;

import java.util.List;

public record GlobalDashboardDto(
        Double averageAccuracy,
        Integer activeStationCount,
        Integer totalForecastsCount,
        List<DailyAccuracyDto> stationLeaderboard,
        List<DailyAccuracyDto> sourceLeaderboard
) {
}
