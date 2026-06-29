package com.example.solar.mapper;

import com.example.solar.dto.ForecastDto;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import org.springframework.stereotype.Component;

@Component
public class ForecastMapper {
    public static ForecastDto toDto(ForecastData forecastData) {
        return new ForecastDto(forecastData.getStation().getId(), forecastData.getTimestamp(), forecastData.getSourceName(), forecastData.getValue());
    }

    public static ForecastData toEntity(ForecastDto dto, Station station) {
        return ForecastData.builder()
                .station(station)
                .sourceName(dto.sourceName())
                .timestamp(dto.timestamp())
                .value(dto.value())
                .build();
    }
}
