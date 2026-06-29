package com.example.solar.provider;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.StationPanel;

import java.util.List;

public interface ForecastProvider {
    SourceNames getSourceName();

    List<ForecastData> fetch(Station station);

    String buildUrl(Station station, StationPanel panel);
}
