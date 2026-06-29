package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Station;
import com.example.solar.model.Task;
import com.example.solar.provider.ForecastProvider;
import com.example.solar.repository.ForecastRepository;
import com.example.solar.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ForecastRunnerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ForecastRepository forecastRepository;

    @Mock
    private ForecastProvider actualProvider;

    @Mock
    private ForecastProvider openMeteoProvider;

    private ForecastRunner forecastRunner;

    @BeforeEach
    void setUp() {
        List<ForecastProvider> providers = List.of(actualProvider, openMeteoProvider);
        forecastRunner = new ForecastRunner(providers, taskRepository, forecastRepository);
    }

    @Test
    void runHourlyActualMonitoring_ShouldExecuteOnlyActualTasks() {
        Station station = new Station();

        Task actualTask = new Task();
        actualTask.setSourceName(SourceNames.ACTUAL);
        actualTask.setStation(station);

        Task forecastTask = new Task();
        forecastTask.setSourceName(SourceNames.OPEN_METEO);
        forecastTask.setStation(station);

        Mockito.when(taskRepository.findTasksByIsActiveTrue()).thenReturn(List.of(actualTask, forecastTask));

        Mockito.when(actualProvider.getSourceName()).thenReturn(SourceNames.ACTUAL);

        List<ForecastData> mockData = List.of(ForecastData.builder().value(3.4).build());
        Mockito.when(actualProvider.fetch(station)).thenReturn(mockData);

        forecastRunner.runHourlyActualMonitoring();

        Mockito.verify(actualProvider, Mockito.times(1)).fetch(station);
        Mockito.verify(openMeteoProvider, Mockito.never()).fetch(Mockito.any());
        Mockito.verify(forecastRepository, Mockito.times(1)).saveAll(mockData);
    }

    @Test
    void runDailyForecasts_ShouldExecuteOnlyNonActualTasks() {
        Station station = new Station();

        Task actualTask = new Task();
        actualTask.setSourceName(SourceNames.ACTUAL);
        actualTask.setStation(station);

        Task forecastTask = new Task();
        forecastTask.setSourceName(SourceNames.OPEN_METEO);
        forecastTask.setStation(station);

        Mockito.when(taskRepository.findTasksByIsActiveTrue()).thenReturn(List.of(actualTask, forecastTask));

        Mockito.when(actualProvider.getSourceName()).thenReturn(SourceNames.ACTUAL);
        Mockito.when(openMeteoProvider.getSourceName()).thenReturn(SourceNames.OPEN_METEO);

        List<ForecastData> mockData = List.of(ForecastData.builder().value(1.2).build());
        Mockito.when(openMeteoProvider.fetch(station)).thenReturn(mockData);

        forecastRunner.runDailyForecasts();

        Mockito.verify(openMeteoProvider, Mockito.times(1)).fetch(station);
        Mockito.verify(actualProvider, Mockito.never()).fetch(Mockito.any());
        Mockito.verify(forecastRepository, Mockito.times(1)).saveAll(mockData);
    }

    @Test
    void executeTask_ShouldDoNothingIfMatchingProviderNotFound() {
        Station station = new Station();
        Task unknownTask = new Task();
        unknownTask.setSourceName(SourceNames.FORECAST_SOLAR);
        unknownTask.setStation(station);

        Mockito.when(actualProvider.getSourceName()).thenReturn(SourceNames.ACTUAL);
        Mockito.when(openMeteoProvider.getSourceName()).thenReturn(SourceNames.OPEN_METEO);

        forecastRunner.executeTask(unknownTask);

        Mockito.verify(actualProvider, Mockito.never()).fetch(Mockito.any());
        Mockito.verify(openMeteoProvider, Mockito.never()).fetch(Mockito.any());
        Mockito.verify(forecastRepository, Mockito.never()).saveAll(Mockito.any());
    }
}