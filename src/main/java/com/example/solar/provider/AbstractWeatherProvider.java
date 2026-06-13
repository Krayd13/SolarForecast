package com.example.solar.provider;

import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractWeatherProvider implements ForecastProvider{
    protected static final double WATT_TO_KW = 1000.0;
    protected static final double SYSTEM_EFFICIENCY = 0.85;
    private static final Logger log = LoggerFactory.getLogger(OpenMeteoProvider.class);

    public abstract List<ForecastData> fetch(Station station);

    protected abstract String buildUrlForPanel(Station station, StationPanel panel);
}
