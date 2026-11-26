package com.example.tasks.infrastructure.rest;

import com.example.tasks.application.TaskService;
import com.example.tasks.domain.Task;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
class TaskController {

    private final TaskService service;

    TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) {
        Task task = service.create(request.title());
        return ResponseEntity.created(URI.create("/api/v1/tasks/" + task.id())).body(TaskResponse.from(task));
    }

    @GetMapping
    List<TaskResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return service.list(page, size).stream().map(TaskResponse::from).toList();
    }

    @GetMapping("/{id}")
    TaskResponse get(@PathVariable UUID id) {
        return TaskResponse.from(service.get(id));
    }

    @PutMapping("/{id}")
    TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        return TaskResponse.from(service.update(id, request.title(), request.done()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    record CreateTaskRequest(
            @NotBlank(message = "Title is required")
            @Size(max = Task.TITLE_MAX_LENGTH, message = "Title must be at most {max} characters")
            String title) {}

    record UpdateTaskRequest(
            @NotBlank(message = "Title is required")
            @Size(max = Task.TITLE_MAX_LENGTH, message = "Title must be at most {max} characters")
            String title,

            @NotNull(message = "Done is required") Boolean done) {}

    record TaskResponse(UUID id, String title, boolean done, Instant createdAt) {

        static TaskResponse from(Task task) {
            return new TaskResponse(task.id(), task.title(), task.done(), task.createdAt());
        }
    }
}
