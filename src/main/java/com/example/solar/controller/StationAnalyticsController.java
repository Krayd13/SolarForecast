package com.example.solar.controller;

import com.example.solar.dto.DayDetailsDto;
import com.example.solar.dto.StationAnalyticsSummaryDto;
import com.example.solar.service.StationAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics/stations")
public class StationAnalyticsController {
    private final StationAnalyticsService analyticsService;

    public StationAnalyticsController(StationAnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/{stationId}/summary")
    public ResponseEntity<StationAnalyticsSummaryDto> getStationSummary(@PathVariable Long stationId) {
        return ResponseEntity.ok(analyticsService.getStationAnalyticsSummary(stationId));
    }

    @GetMapping("/{stationId}/day-details")
    public ResponseEntity<DayDetailsDto> getDayDetails(@PathVariable Long stationId,
                                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(analyticsService.getDayDetails(stationId, date));
    }
}
