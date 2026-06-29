package com.example.solar.controller;

import com.example.solar.dto.StationDto;
import com.example.solar.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public ResponseEntity<List<StationDto>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationDto> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.findStation(id));
    }

    @PostMapping
    public ResponseEntity<StationDto> createStation(@Valid @RequestBody StationDto station) {
        return ResponseEntity.ok(stationService.createStation(station));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationDto> updateStation(@PathVariable Long id, @Valid @RequestBody StationDto station) {
        return ResponseEntity.ok(stationService.updateStation(id, station));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
