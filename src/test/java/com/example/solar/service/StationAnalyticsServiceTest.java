package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.dto.DayDetailsDto;
import com.example.solar.model.DailyAccuracy;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.repository.ForecastAccuracyRepository;
import com.example.solar.repository.ForecastRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StationAnalyticsServiceTest {
    @Mock
    private ForecastAccuracyRepository forecastAccuracyRepository;
    @Mock
    private ForecastRepository forecastRepository;
    @InjectMocks
    private StationAnalyticsService analyticsService;

    @Test
    void getDayDetails_shouldReturnCorrectDayDetails_whenDataIsValid() {
        Long stationId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 29);
        LocalDateTime time = date.atTime(12, 0);
        Station station = new Station();
        station.setId(stationId);

        ForecastData actual = ForecastData.builder().station(station).sourceName(SourceNames.ACTUAL).timestamp(time).value(5.0).build();
        ForecastData forecast = ForecastData.builder().station(station).sourceName(SourceNames.FORECAST_SOLAR).timestamp(time).value(3.5).build();

        Mockito.when(forecastRepository.findAllByStationIdAndTimestampBetween(Mockito.eq(stationId), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(actual, forecast));

        DailyAccuracy dailyAccuracy = DailyAccuracy.builder()
                .sourceName(SourceNames.FORECAST_SOLAR)
                .mape(5.0)
                .rmse(1.0)
                .build();

        Mockito.when(forecastAccuracyRepository.findAllByStationIdAndDate(stationId, date))
                .thenReturn(List.of(dailyAccuracy));

        DayDetailsDto details = analyticsService.getDayDetails(stationId, date);

        Assertions.assertNotNull(details);
        Assertions.assertEquals(SourceNames.FORECAST_SOLAR, details.bestSource(), "Невірно визначено найкраще джерело");
        Assertions.assertEquals(95.0, details.dayAccuracy(), "Точність дня має бути 100 - MAPE (100 - 5 = 95)");

        Assertions.assertEquals(1.5, details.maxDeviation());
        Assertions.assertEquals(1.5, details.averageError());

        Assertions.assertTrue(details.hourlyErrors().containsKey(SourceNames.FORECAST_SOLAR));
        Assertions.assertEquals(1.5, details.hourlyErrors().get(SourceNames.FORECAST_SOLAR).get(0));
    }

    @Test
    void getDayDetails_shouldReturnDefaultDto_whenActualDataIsMissing() {
        Long stationId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 29);
        Station station = new Station();
        station.setId(stationId);

        ForecastData forecastOnly = ForecastData.builder().station(station).sourceName(SourceNames.FORECAST_SOLAR).timestamp(date.atTime(12, 0)).value(4.0).build();

        Mockito.when(forecastRepository.findAllByStationIdAndTimestampBetween(Mockito.eq(stationId), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(forecastOnly));

        DayDetailsDto details = analyticsService.getDayDetails(stationId, date);

        Assertions.assertNotNull(details);
        Assertions.assertEquals(100.0, details.dayAccuracy());
        Assertions.assertEquals(SourceNames.ACTUAL, details.bestSource());
        Assertions.assertEquals(0.0, details.averageError());
        Assertions.assertTrue(details.hourlyErrors().isEmpty());
    }
}
