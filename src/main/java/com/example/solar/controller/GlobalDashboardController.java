package com.example.solar.controller;

import com.example.solar.dto.GlobalDashboardDto;
import com.example.solar.service.GlobalDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics/global")
public class GlobalDashboardController {
    private final GlobalDashboardService dashboardService;

    public GlobalDashboardController(GlobalDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<GlobalDashboardDto> getGlobalDashboard(){
        return ResponseEntity.ok(dashboardService.getGlobalDashboard());
    }
}
