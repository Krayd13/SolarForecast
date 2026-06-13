package com.example.solar.mapper;

import com.example.solar.dto.TaskDto;
import com.example.solar.model.Station;
import com.example.solar.model.Task;
import com.example.solar.repository.StationRepository;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public static TaskDto toDto(Task task){
        return new TaskDto(task.getStation().getId(), task.getSourceName(), task.getStartDate(), task.getEndDate(), task.getActive());
    }

    public static Task toEntity(TaskDto dto, Station station){
        return new Task.Builder()
                .station(station)
                .sourceName(dto.sourceName())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .isActive(dto.isActive())
                .build();
    }
}
