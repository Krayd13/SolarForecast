package com.example.solar.mapper;

import com.example.solar.dto.StationDto;
import com.example.solar.dto.StationPanelDto;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StationMapper {
    public static StationDto toDto(Station station){
        List<StationPanelDto> panels = (station.getPanels() == null) ? List.of() :
                station.getPanels().stream().map(StationPanelMapper::toDto).toList();
        return new StationDto(station.getName(), station.getLatitude(), station.getLongitude(), panels);
    }

    public static Station toEntity(StationDto dto){
        Station station = Station.builder()
                .name(dto.name())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .build();

        List<StationPanel> panels = (dto.panels() == null) ? List.of() : dto.panels().stream()
                .map(p -> StationPanelMapper.toEntity(p, station)).toList();
        station.setPanels(panels);
        return station;
    }
}
