package com.example.solar.scheduler;

import com.example.solar.service.ForecastRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ForecastScheduler {
    private final ForecastRunner forecastRunner;
    private static final Logger log = LoggerFactory.getLogger(ForecastScheduler.class);

    public ForecastScheduler(ForecastRunner forecastRunner) {
        this.forecastRunner = forecastRunner;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runHourlyForecastsAndMonitoringUpdate() {
        log.info("Cron: Збір фактичної генерації за останню годину...");
        try {
            forecastRunner.runHourlyActualMonitoring();
        } catch (Exception e) {
            log.error("Помилка щогодинного збору фактичних даних: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void collectDailyForecasts() {
        log.info("Cron: Запуск щоденного оновлення прогнозів погоди...");
        try {
            forecastRunner.runDailyForecasts();
            log.info("Щоденні прогнози успішно завантажено.");
        } catch (Exception e) {
            log.error("Помилка під час завантаження щоденних прогнозів: {}", e.getMessage());
        }
    }
}
