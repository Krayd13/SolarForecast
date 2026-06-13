package com.example.solar.controller;

import com.example.solar.dto.TaskDto;
import com.example.solar.service.ForecastRunner;
import com.example.solar.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/active")
    public ResponseEntity<List<TaskDto>> getActiveTasks(){
        return ResponseEntity.ok(taskService.getAllActiveTasks());
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto task){
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskDto> changeActivity(@PathVariable Long id){
        return ResponseEntity.ok(taskService.changeActivity(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
