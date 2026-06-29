package com.example.solar.service;

import com.example.solar.dto.DailyAccuracyDto;
import com.example.solar.dto.GlobalDashboardDto;
import com.example.solar.repository.ForecastAccuracyRepository;
import com.example.solar.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GlobalDashboardService {
    private final StationRepository stationRepository;
    private final ForecastAccuracyRepository accuracyRepository;

    public GlobalDashboardService(StationRepository stationRepository, ForecastAccuracyRepository accuracyRepository) {
        this.stationRepository = stationRepository;
        this.accuracyRepository = accuracyRepository;
    }

    public GlobalDashboardDto getGlobalDashboard() {
        int activeStationCount = (int) stationRepository.count();
        Double averageMape = accuracyRepository.getGlobalAverageMape();
        double totalAverageAccuracy = (averageMape != null) ? (100 - averageMape) : 100.0;

        int totalForecastCount = (int) accuracyRepository.count();

        List<DailyAccuracyDto> stationLeaderboard = accuracyRepository.getStationLeaderboard();
        List<DailyAccuracyDto> sourceLeaderboard = accuracyRepository.getSourceLeaderboard();

        return new GlobalDashboardDto(Math.round(totalAverageAccuracy * 10.0) / 10.0, activeStationCount,
                totalForecastCount, stationLeaderboard, sourceLeaderboard);
    }
}
