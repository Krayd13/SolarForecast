package com.example.solar.scheduler;

import com.example.solar.model.Station;
import com.example.solar.repository.StationRepository;
import com.example.solar.service.DailyAnalyticProcessor;
import com.example.solar.service.ForecastRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SolarAnalyticsScheduler {
    private final ForecastRunner runner;
    private static final Logger log = LoggerFactory.getLogger(SolarAnalyticsScheduler.class);
    private final StationRepository stationRepository;
    private final DailyAnalyticProcessor analyticProcessor;

    public SolarAnalyticsScheduler(ForecastRunner runner, StationRepository stationRepository, DailyAnalyticProcessor analyticProcessor) {
        this.runner = runner;
        this.stationRepository = stationRepository;
        this.analyticProcessor = analyticProcessor;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyAnalyticsComputation() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Station> stations = stationRepository.findAll();

        for (Station station : stations) {
            try {
                analyticProcessor.calculateAndSaveDailyAccuracyForAllSources(station.getId(), yesterday);
            } catch (Exception e) {
                log.error("Критичний збій під час фонового прорахунку станції ID {}: {}", station.getId(), e.getMessage());
            }
        }
        log.info("Фоновий розрахунок аналітики за {} успішно завершено", yesterday);
    }
}
