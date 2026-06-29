package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.dto.DailyAccuracyDto;
import com.example.solar.dto.DayDetailsDto;
import com.example.solar.dto.ForecastDto;
import com.example.solar.dto.StationAnalyticsSummaryDto;
import com.example.solar.mapper.ForecastMapper;
import com.example.solar.model.DailyAccuracy;
import com.example.solar.repository.ForecastAccuracyRepository;
import com.example.solar.repository.ForecastRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StationAnalyticsService {
    private final ForecastAccuracyRepository accuracyRepository;
    private final ForecastRepository forecastRepository;

    public StationAnalyticsService(ForecastAccuracyRepository accuracyRepository, ForecastRepository forecastRepository) {
        this.accuracyRepository = accuracyRepository;
        this.forecastRepository = forecastRepository;
    }

    public StationAnalyticsSummaryDto getStationAnalyticsSummary(Long stationId) {
        List<DailyAccuracy> accuracyList = accuracyRepository.findAllByStationId(stationId);
        double averageMape = accuracyList.stream().map(DailyAccuracy::getMape).mapToDouble(Double::doubleValue).average()
                .orElse(0.0);
        double averageRmse = accuracyList.stream().map(DailyAccuracy::getRmse).mapToDouble(Double::doubleValue).average()
                .orElse(0.0);
        List<DailyAccuracyDto> sourcesAccuracy = accuracyRepository.getAverageSourcesAccuracyByStationId(stationId);
        return new StationAnalyticsSummaryDto(100 - averageMape, averageMape, averageRmse, accuracyList.size(), sourcesAccuracy);
    }

    public DayDetailsDto getDayDetails(Long stationId, LocalDate date) {
        Map<SourceNames, List<ForecastDto>> hourlyData = getHourlyGeneration(stationId, date);
        List<ForecastDto> actualData = hourlyData != null ? hourlyData.remove(SourceNames.ACTUAL) : null;

        if (isDataMissing(hourlyData, actualData)) {
            return new DayDetailsDto(100.0, SourceNames.ACTUAL, 0.0, 0.0, Map.of());
        }

        HourlyCalculation metrics = calculateHourlyMetrics(hourlyData, actualData);

        List<DailyAccuracy> accuracyList = accuracyRepository.findAllByStationIdAndDate(stationId, date);
        double dayAccuracy = Math.round(calculateDayAccuracy(accuracyList) * 100.0) / 100.0;
        SourceNames bestSource = findBestSource(accuracyList);


        return new DayDetailsDto(dayAccuracy, bestSource, metrics.averageError(), metrics.maxDeviation(), metrics.hourlyErrors());
    }

    private Map<SourceNames, List<ForecastDto>> getHourlyGeneration(Long stationId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<ForecastDto> forecasts = forecastRepository.findAllByStationIdAndTimestampBetween(stationId, start, end).stream().map(ForecastMapper::toDto).toList();
        Map<SourceNames, List<ForecastDto>> result = forecasts.stream().collect(Collectors.groupingBy(ForecastDto::sourceName));

        return result;
    }

    private record HourlyCalculation(double maxDeviation, double averageError,
                                     Map<SourceNames, List<Double>> hourlyErrors) {
    }

    private HourlyCalculation calculateHourlyMetrics(Map<SourceNames, List<ForecastDto>> hourlyData, List<ForecastDto> actualData) {
        Map<SourceNames, List<Double>> result = new HashMap<>();
        double maxDeviation = 0;
        double sumErrors = 0;
        int totalCalculatedPoints = 0;

        for (Map.Entry<SourceNames, List<ForecastDto>> entry : hourlyData.entrySet()) {
            List<Double> deltaList = new ArrayList<>();
            List<ForecastDto> forecastList = entry.getValue();
            int hoursToCompare = Math.min(forecastList.size(), actualData.size());

            for (int i = 0; i < hoursToCompare; i++) {
                double delta = Math.abs(entry.getValue().get(i).value() - actualData.get(i).value());
                sumErrors += delta;
                if (delta > maxDeviation) {
                    maxDeviation = delta;
                }
                deltaList.add(Math.round(delta * 100.0) / 100.0);
            }
            totalCalculatedPoints += hoursToCompare;
            result.put(entry.getKey(), deltaList);
        }
        double averageError = totalCalculatedPoints > 0 ? (sumErrors / totalCalculatedPoints) : 0.0;

        return new HourlyCalculation(Math.round(maxDeviation * 100.0) / 100.0,
                Math.round(averageError * 100.0) / 100.0,
                result);
    }

    private boolean isDataMissing(Map<SourceNames, List<ForecastDto>> hourlyData, List<ForecastDto> actualData) {
        return actualData == null || actualData.isEmpty() || hourlyData.isEmpty();
    }

    private double calculateDayAccuracy(List<DailyAccuracy> accuracyList) {
        double averageMape = accuracyList.stream().mapToDouble(DailyAccuracy::getMape).average().orElse(0.0);
        return 100 - averageMape;
    }

    private SourceNames findBestSource(List<DailyAccuracy> accuracyList) {
        SourceNames bestSource = accuracyList.stream()
                .min(Comparator.comparing(DailyAccuracy::getMape)).map(DailyAccuracy::getSourceName).orElse(SourceNames.ACTUAL);
        return bestSource;
    }
}
