package com.example.tasks.application;

import com.example.tasks.domain.Task;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    void save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findPage(int page, int size);

    boolean deleteById(UUID id);
}
