package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.model.DailyAccuracy;
import com.example.solar.model.ForecastData;
import com.example.solar.repository.ForecastAccuracyRepository;
import com.example.solar.repository.ForecastRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DailyAnalyticProcessorTest {
    @Mock
    private ForecastAccuracyRepository accuracyRepository;
    @Mock
    private ForecastRepository forecastRepository;
    @InjectMocks
    private DailyAnalyticProcessor dailyAnalyticProcessor;

    @Test
    void calculateAndSaveDailyAccuracyForAllSources_shouldCalculateMapeAndRmseCorrectly_whenDataIsAvailable() {
        Long stationId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 29);
        LocalDateTime time1 = date.atTime(12, 0);
        LocalDateTime time2 = date.atTime(13, 0);

        Mockito.when(forecastRepository.findDistinctSourceNamesByStationIdAndTimestampBetween(Mockito.eq(stationId), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(SourceNames.FORECAST_SOLAR.name()));

        List<ForecastData> actualList = List.of(
                ForecastData.builder().timestamp(time1).value(10.0).build(),
                ForecastData.builder().timestamp(time2).value(0.0).build()
        );

        List<ForecastData> forecastList = List.of(
                ForecastData.builder().timestamp(time1).value(8.0).build(),
                ForecastData.builder().timestamp(time2).value(1.0).build()
        );

        Mockito.when(forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(Mockito.eq(stationId), Mockito.eq(SourceNames.ACTUAL), Mockito.any(), Mockito.any()))
                .thenReturn(actualList);
        Mockito.when(forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(Mockito.eq(stationId), Mockito.eq(SourceNames.FORECAST_SOLAR), Mockito.any(), Mockito.any()))
                .thenReturn(forecastList);

        ArgumentCaptor<DailyAccuracy> accuracyCaptor = ArgumentCaptor.forClass(DailyAccuracy.class);

        dailyAnalyticProcessor.calculateAndSaveDailyAccuracyForAllSources(stationId, date);

        Mockito.verify(accuracyRepository, Mockito.times(1)).save(accuracyCaptor.capture());
        DailyAccuracy savedAccuracy = accuracyCaptor.getValue();

        Assertions.assertNotNull(savedAccuracy);
        Assertions.assertEquals(SourceNames.FORECAST_SOLAR, savedAccuracy.getSourceName());
        Assertions.assertEquals(date, savedAccuracy.getDate());
        Assertions.assertEquals(10.0, savedAccuracy.getMape(), 0.001, "MAPE пораховано некоректно");
        Assertions.assertEquals(Math.sqrt(2.5), savedAccuracy.getRmse(), 0.001, "RMSE пораховано некоректно");
    }

    @Test
    void calculateAndSaveDailyAccuracyForAllSources_shouldNotThrowException_WhenDataIsMissingForOneSource() {
        Long stationId = 1L;
        LocalDate date = LocalDate.of(2026, 6, 29);

        Mockito.when(forecastRepository.findDistinctSourceNamesByStationIdAndTimestampBetween(Mockito.eq(stationId), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(SourceNames.FORECAST_SOLAR.name()));
        Mockito.when(forecastRepository.findAllByStationIdAndSourceNameAndTimestampBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of());
        Assertions.assertDoesNotThrow(() -> dailyAnalyticProcessor.calculateAndSaveDailyAccuracyForAllSources(stationId, date));
        Mockito.verify(accuracyRepository, Mockito.never()).save(Mockito.any());
    }
}
