package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.model.ForecastData;
import com.example.solar.model.Task;
import com.example.solar.provider.ForecastProvider;
import com.example.solar.repository.ForecastRepository;
import com.example.solar.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForecastRunner {
    private final List<ForecastProvider> providers;
    private final TaskRepository taskRepository;
    private final ForecastRepository forecastRepository;

    public ForecastRunner(List<ForecastProvider> providers, TaskRepository repository, ForecastRepository forecastRepository) {
        this.providers = providers;
        this.taskRepository = repository;
        this.forecastRepository = forecastRepository;
    }

    public void runHourlyActualMonitoring() {
        List<Task> tasks = taskRepository.findTasksByIsActiveTrue();
        for (Task task : tasks) {
            if (task.getSourceName() == SourceNames.ACTUAL) {
                executeTask(task);
            }
        }
    }

    public void runDailyForecasts() {
        List<Task> tasks = taskRepository.findTasksByIsActiveTrue();
        for (Task task : tasks) {
            if (task.getSourceName() != SourceNames.ACTUAL) {
                executeTask(task);
            }
        }
    }

    public void executeTask(Task task) {
        providers.stream()
                .filter(p -> p.getSourceName().equals(task.getSourceName()))
                .findFirst()
                .ifPresent(provider -> {
                    List<ForecastData> data = provider.fetch(task.getStation());
                    forecastRepository.saveAll(data);
                });
    }
}
