package com.example.solar.mapper;

import com.example.solar.dto.StationPanelDto;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import org.springframework.stereotype.Component;

@Component
public class StationPanelMapper {
    public static StationPanelDto toDto(StationPanel stationPanel) {
        return new StationPanelDto(stationPanel.getStation().getId(), stationPanel.getAzimuth(), stationPanel.getTilt(), stationPanel.getCapacity());
    }

    public static StationPanel toEntity(StationPanelDto dto, Station station) {
        return StationPanel.builder()
                .station(station)
                .azimuth(dto.azimuth())
                .tilt(dto.tilt())
                .capacity(dto.capacity())
                .build();
    }
}
