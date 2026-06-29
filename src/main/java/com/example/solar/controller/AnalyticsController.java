package com.example.solar.controller;

import com.example.solar.service.DailyAnalyticProcessor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final DailyAnalyticProcessor processor;

    public AnalyticsController(DailyAnalyticProcessor processor) {
        this.processor = processor;
    }

    @PostMapping("/recalculate")
    public ResponseEntity<String> triggerAnalytics(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        processor.calculateAndSaveDailyAccuracyForAllSources(stationId, date);
        return ResponseEntity.ok("Recalculation triggered! Check your database logs.");

    }
}
