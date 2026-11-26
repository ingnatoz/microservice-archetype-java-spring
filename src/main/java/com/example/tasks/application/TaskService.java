package com.example.tasks.application;

import com.example.tasks.domain.Task;
import java.time.Clock;
import java.util.List;
import java.util.UUID;

public class TaskService {

    private final TaskRepository repository;
    private final Clock clock;

    public TaskService(TaskRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Task create(String title) {
        Task task = Task.create(title, clock.instant());
        repository.save(task);
        return task;
    }

    public Task get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    public List<Task> list(int page, int size) {
        return repository.findPage(page, size);
    }

    public Task update(UUID id, String title, boolean done) {
        Task task = get(id).update(title, done);
        repository.save(task);
        return task;
    }

    public void delete(UUID id) {
        if (!repository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }
}
