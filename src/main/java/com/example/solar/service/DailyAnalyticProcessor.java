package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.dto.DailyAccuracyDto;
import com.example.solar.mapper.AccuracyMapper;
import com.example.solar.model.DailyAccuracy;
import com.example.solar.model.ForecastData;
import com.example.solar.repository.ForecastAccuracyRepository;
import com.example.solar.repository.ForecastRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DailyAnalyticProcessor {
    private final ForecastAccuracyRepository accuracyRepository;
    private final ForecastRepository forecastRepository;
    private static final Logger log = LoggerFactory.getLogger(DailyAnalyticProcessor.class);

    public DailyAnalyticProcessor(ForecastAccuracyRepository accuracyRepository, ForecastRepository forecastRepository) {
        this.accuracyRepository = accuracyRepository;
        this.forecastRepository = forecastRepository;
    }

    @Transactional
    public void calculateAndSaveDailyAccuracyForAllSources(Long stationId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<SourceNames> sourceNames = forecastRepository.findDistinctSourceNamesByStationIdAndTimestampBetween(stationId, start, end).stream()
                .map(SourceNames::valueOf).filter(name -> name != SourceNames.ACTUAL).toList();

        for (SourceNames name : sourceNames) {
            try {
                calculateAndSaveDailyAccuracy(stationId, name, date);
                log.info("Успішно пораховано аналітику за {} джерела {}", date, name.name());
            } catch (Exception e) {
                log.error("Помилка розрахунку аналітики за {} джерела {}: {}", date, name.name(), e.getMessage());
            }
        }
    }

    @Transactional
    private DailyAccuracyDto calculateAndSaveDailyAccuracy(Long stationId, SourceNames sourceName, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<ForecastData> actualList = forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(stationId, SourceNames.ACTUAL, start, end);
        List<ForecastData> forecastList = forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(stationId, sourceName, start, end);

        log.info("Результат запиту до БД: actualList.size={}, forecastList.size={}",
                actualList != null ? actualList.size() : "NULL-СПИСОК",
                forecastList != null ? forecastList.size() : "NULL-СПИСОК");

        if (actualList.isEmpty() || forecastList.isEmpty()) {
            throw new EntityNotFoundException("No data for analytic");
        }

        Map<LocalDateTime, Double> actualMap = actualList.stream()
                .collect(Collectors.toMap(ForecastData::getTimestamp, ForecastData::getValue));

        AccuracyMetrics metrics = calculateMetrics(forecastList, actualMap);
        DailyAccuracy dailyAccuracy = saveDailyAccuracy(stationId, sourceName, date, metrics);

        return AccuracyMapper.toDto(dailyAccuracy);
    }

    private record AccuracyMetrics(Double mape, Double rmse) {
    }

    private AccuracyMetrics calculateMetrics(List<ForecastData> forecastList, Map<LocalDateTime, Double> actualMap) {
        double sumPercentageError = 0;
        double sumAbsoluteSquareError = 0;
        int matchedPoints = 0;
        for (ForecastData forecast : forecastList) {
            Double actualValue = actualMap.get(forecast.getTimestamp());
            if (actualValue != null) {
                double absError = Math.abs(actualValue - forecast.getValue());
                double pctError = (actualValue == 0) ? 0.0 : absError / actualValue;
                sumPercentageError += pctError;
                sumAbsoluteSquareError += Math.pow(absError, 2);
                matchedPoints++;
            }

        }

        if (matchedPoints == 0) {
            throw new IllegalArgumentException("No overlapping timestamps between actual and forecast data");
        }

        double mape = (sumPercentageError / matchedPoints) * 100;
        double rmse = Math.sqrt(sumAbsoluteSquareError / matchedPoints);

        return new AccuracyMetrics(mape, rmse);
    }

    private DailyAccuracy saveDailyAccuracy(Long stationId, SourceNames sourceName, LocalDate date, AccuracyMetrics metrics) {
        DailyAccuracy dailyAccuracy = DailyAccuracy.builder()
                .stationId(stationId)
                .sourceName(sourceName)
                .date(date)
                .mape(metrics.mape())
                .rmse(metrics.rmse())
                .build();
        return accuracyRepository.save(dailyAccuracy);
    }


}
