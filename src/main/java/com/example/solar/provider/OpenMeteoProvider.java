package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import com.example.solar.provider.dto.OpenMeteoDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenMeteoProvider extends AbstractWeatherProvider{

    private static final Logger log = LoggerFactory.getLogger(OpenMeteoProvider.class);
    private final RestTemplate restTemplate;

    public OpenMeteoProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public SourceNames getSourceName() {
        return SourceNames.OPEN_METEO;
    }

    @Override
    public List<ForecastData> fetch(Station station) {
        OpenMeteoDto response = restTemplate.getForObject(buildUrlForPanel(station, null), OpenMeteoDto.class);
        if (response == null || response.hourlyData() == null){
            log.warn("Отримано порожню відповідь від API для станції {}", station.getId());
            return new ArrayList<>();
        }

        double totalCapacity = calculateTotalCapacity(station);

        return mapToForecast(station, response, totalCapacity);

    }

    @Override
    protected String buildUrlForPanel(Station station, StationPanel panel) {
        return String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&hourly=shortwave_radiation&forecast_days=1",
                station.getLatitude(),
                station.getLongitude()
        );
    }

    private double calculatePower(double radiation, double totalCapacity) {
        double efficiencyFactor = radiation / AbstractWeatherProvider.WATT_TO_KW;
        return Math.max(0, totalCapacity * efficiencyFactor * AbstractWeatherProvider.SYSTEM_EFFICIENCY);
    }

    private double calculateTotalCapacity(Station station) {
        double totalCapacity = station.getPanels().stream()
                .mapToDouble(p -> (p.getCapacity() / AbstractWeatherProvider.WATT_TO_KW))
                .sum();
        return (totalCapacity == 0) ? 5.0 : totalCapacity;
    }

    private List<ForecastData> mapToForecast(Station station, OpenMeteoDto response, double totalCapacity) {
        List<ForecastData> result = new ArrayList<>();
        OpenMeteoDto.HourlyData hourlyData = response.hourlyData();

        for (int i = 0; i < hourlyData.time().size(); i++) {
            double radiation = hourlyData.shortwaveRadiation().get(i);
            double value = calculatePower(radiation, totalCapacity);

            result.add(ForecastData.builder()
                    .station(station)
                    .sourceName(getSourceName())
                    .timestamp(LocalDateTime.parse(hourlyData.time().get(i)))
                    .value(value)
                    .build());
        }

        log.info("Успішно згенеровано {} записів прогнозу на сьогодні.", result.size());
        return result;
    }

}
