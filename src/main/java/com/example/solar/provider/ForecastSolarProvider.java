package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import com.example.solar.provider.dto.ForecastSolarDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ForecastSolarProvider implements ForecastProvider{
    private static final double WATT_TO_KW = 1000.0;

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(ForecastSolarProvider.class);

    public ForecastSolarProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SourceNames getSourceName() {
        return SourceNames.FORECAST_SOLAR;
    }

    @Override
    public List<ForecastData> fetch(Station station) {
        Map<LocalDateTime, Double> aggregatedData = fetchAndAggregate(station);
        return mapToForecast(station, aggregatedData);
    }

    @Override
    public String buildUrlForPanel(Station station, StationPanel panel) {
        return String.format(
                "https://api.forecast.solar/estimate/%s/%s/%s/%s/%s",
                station.getLatitude(),
                station.getLongitude(),
                panel.getTilt(),
                panel.getAzimuth(),
                panel.getCapacity() / WATT_TO_KW
        );
    }

    private void fetchPanelData(Station station, StationPanel panel, Map<LocalDateTime, Double> aggregatedData) {
        String url = buildUrlForPanel(station, panel);
        ForecastSolarDto response = restTemplate.getForObject(url, ForecastSolarDto.class);

        if (response == null || response.result() == null) {
            log.warn("Отримано порожню відповідь від API для станції {}", station.getId());
            return;
        }

        response.result().wattHoursPeriod().forEach((timeStr, value) -> {
            LocalDateTime time = LocalDateTime.parse(timeStr.replace(" ", "T"));
            aggregatedData.merge(time, value, Double::sum);
        });
    }

    private Map<LocalDateTime, Double> fetchAndAggregate(Station station){
        Map<LocalDateTime, Double> aggregatedData = new HashMap<>();

        for(StationPanel panel : station.getPanels()){
            fetchPanelData(station, panel, aggregatedData);
        }
        return  aggregatedData;
    }

    private List<ForecastData> mapToForecast(Station station, Map<LocalDateTime, Double> aggregatedData) {
        return aggregatedData.entrySet().stream()
                .map(e -> ForecastData.builder()
                        .station(station)
                        .sourceName(getSourceName())
                        .timestamp(e.getKey())
                        .value(e.getValue() / WATT_TO_KW)
                        .build()).toList();
    }
}
