package com.example.solar.service;

import com.example.solar.SourceNames;
import com.example.solar.dto.TaskDto;
import com.example.solar.mapper.TaskMapper;
import com.example.solar.model.Station;
import com.example.solar.model.Task;
import com.example.solar.repository.StationRepository;
import com.example.solar.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final StationRepository stationRepository;
    private final ForecastRunner forecastRunner;
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository, StationRepository stationRepository, ForecastRunner forecastRunner) {
        this.taskRepository = taskRepository;
        this.stationRepository = stationRepository;
        this.forecastRunner = forecastRunner;
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(TaskMapper::toDto).toList();
    }

    public List<TaskDto> getAllActiveTasks(){
        return taskRepository.findTasksByIsActiveTrue().stream().map(TaskMapper::toDto).toList();
    }

    public TaskDto createTask(TaskDto task){
        Station station = stationRepository.findById(task.stationId()).orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + task.stationId()));
        Task saved = taskRepository.save(TaskMapper.toEntity(task, station));
        try{
            if(task.sourceName() == SourceNames.ACTUAL){
                forecastRunner.runHourlyActualMonitoring();
                log.info("Створено завдання моніторингу. Миттєво запущено збір фактичних даних");
            } else{
                forecastRunner.runDailyForecasts();
                log.info("Створено завдання прогнозу. Миттєво запущено стягування прогнозів з API");
            }
        } catch (Exception e){
            log.error("Завдання успішно створено, але виникла помилка первинного збору даних: {}", e.getMessage());
        }
        return TaskMapper.toDto(saved);
    }


    public void deleteTask(Long id) {
        if(!taskRepository.existsById(id)){
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public TaskDto changeActivity(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        task.setActive(!task.getActive());
        return TaskMapper.toDto(taskRepository.save(task));
    }
}
