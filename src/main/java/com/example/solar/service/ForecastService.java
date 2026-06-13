package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.dto.ForecastDto;
import com.example.solar.mapper.ForecastMapper;
import com.example.solar.repository.ForecastRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ForecastService {
    private final ForecastRepository forecastRepository;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }


    public List<ForecastDto> getForecasts(Long stationId, SourceNames sourceName, LocalDateTime from, LocalDateTime to) {
        return forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(stationId, sourceName, from, to).stream()
                .map(ForecastMapper::toDto)
                .toList();
    }


}
