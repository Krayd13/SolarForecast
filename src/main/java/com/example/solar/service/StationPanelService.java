package com.example.solar.service;

import com.example.solar.dto.StationPanelDto;
import com.example.solar.mapper.StationPanelMapper;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import com.example.solar.repository.StationPanelRepository;
import com.example.solar.repository.StationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationPanelService {
    private final StationPanelRepository panelRepository;
    private final StationRepository stationRepository;

    public StationPanelService(StationPanelRepository panelRepository, StationRepository stationRepository) {
        this.panelRepository = panelRepository;
        this.stationRepository = stationRepository;
    }

    public List<StationPanelDto> getPanelsByStationId(Long stationId) {
        return panelRepository.findAllByStationId(stationId).stream()
                .map(StationPanelMapper::toDto)
                .toList();
    }

    public StationPanelDto findPanelById(Long id) {
        StationPanel panel = panelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Station Panel not found by id: " + id));
        return StationPanelMapper.toDto(panel);
    }

    public StationPanelDto createPanel(StationPanelDto panelDto) {
        Station station = stationRepository.findById(panelDto.stationId()).orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + panelDto.stationId()));
        StationPanel saved = panelRepository.save(StationPanelMapper.toEntity(panelDto, station));
        return StationPanelMapper.toDto(saved);
    }

    public StationPanelDto updatePanel(Long id, StationPanelDto panelDto) {
        StationPanel panel = panelRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Station Panel not found by id: " + id));
        Station station = stationRepository.findById(panelDto.stationId()).orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + panelDto.stationId()));
        panel.setStation(station);
        panel.setAzimuth(panelDto.azimuth());
        panel.setTilt(panelDto.tilt());
        panel.setCapacity(panelDto.capacity());
        panelRepository.save(panel);
        return StationPanelMapper.toDto(panel);
    }

    public void deletePanel(Long id) {
        if (!panelRepository.existsById(id)) {
            throw new EntityNotFoundException("Station Panel not found by id: " + id);
        }
        panelRepository.deleteById(id);
    }
}
