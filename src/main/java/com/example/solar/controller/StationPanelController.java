package com.example.solar.controller;

import com.example.solar.dto.StationPanelDto;
import com.example.solar.service.StationPanelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class StationPanelController {
    private final StationPanelService panelService;

    public StationPanelController(StationPanelService panelService) {
        this.panelService = panelService;
    }

    @GetMapping("stations/{station_id}/panels")
    public ResponseEntity<List<StationPanelDto>> getAllPanels(@PathVariable Long stationId) {
        return ResponseEntity.ok(panelService.getPanelsByStationId(stationId));
    }

    @GetMapping("panels/{id}")
    public ResponseEntity<StationPanelDto> getPanelById(@PathVariable Long id) {
        return ResponseEntity.ok(panelService.findPanelById(id));
    }

    @PostMapping("stations/{station_id}/panels")
    public ResponseEntity<StationPanelDto> createPanel(@Valid @RequestBody StationPanelDto panel) {
        return ResponseEntity.ok(panelService.createPanel(panel));
    }

    @PutMapping("/panels/{id}")
    public ResponseEntity<StationPanelDto> updatePanel(@PathVariable Long id, @Valid @RequestBody StationPanelDto panel) {
        return ResponseEntity.ok(panelService.updatePanel(id, panel));
    }

    @DeleteMapping("/panels/{id}")
    public ResponseEntity<Void> deletePanel(@PathVariable Long id) {
        panelService.deletePanel(id);
        return ResponseEntity.noContent().build();
    }
}
