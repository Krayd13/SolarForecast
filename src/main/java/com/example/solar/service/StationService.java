package com.example.solar.service;

import com.example.solar.dto.StationDto;
import com.example.solar.mapper.StationMapper;
import com.example.solar.mapper.StationPanelMapper;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import com.example.solar.repository.ForecastRepository;
import com.example.solar.repository.StationPanelRepository;
import com.example.solar.repository.StationRepository;
import com.example.solar.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StationService {
    private final StationRepository stationRepository;
    private final ForecastRepository forecastRepository;
    private final TaskRepository taskRepository;
    private final StationPanelRepository panelRepository;

    public StationService(StationRepository stationRepository, ForecastRepository forecastRepository, TaskRepository taskRepository, StationPanelRepository panelRepository) {
        this.stationRepository = stationRepository;
        this.forecastRepository = forecastRepository;
        this.taskRepository = taskRepository;
        this.panelRepository = panelRepository;
    }

    public List<StationDto> getAllStations() {
        return stationRepository.findAll().stream()
                .map(StationMapper::toDto).toList();
    }

    public StationDto createStation(StationDto station){
        Station saved = stationRepository.save(StationMapper.toEntity(station));
        return StationMapper.toDto(saved);
    }

    public StationDto findStation(Long id) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
        return StationMapper.toDto(station);
    }

    public StationDto updateStation(Long id, StationDto stationDto) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
        station.setName(stationDto.name());
        station.setLatitude(stationDto.latitude());
        station.setLongitude(stationDto.longitude());
        List<StationPanel> panels = stationDto.panels().stream().map(p -> StationPanelMapper.toEntity(p, station)).toList();
        station.setPanels(panels);
        return StationMapper.toDto(stationRepository.save(station));
    }

    @Transactional
    public void deleteStation(Long id) {
        if(!stationRepository.existsById(id)){
            throw new EntityNotFoundException("Station not found with id: " + id);
        }
        forecastRepository.deleteByStationId(id);
        taskRepository.deleteByStationId(id);
        panelRepository.deleteByStationId(id);
        stationRepository.deleteById(id);
    }
}
