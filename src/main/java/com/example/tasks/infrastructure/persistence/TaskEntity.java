package com.example.tasks.infrastructure.persistence;

import com.example.tasks.domain.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
class TaskEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = Task.TITLE_MAX_LENGTH)
    private String title;

    @Column(nullable = false)
    private boolean done;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected TaskEntity() {}

    private TaskEntity(UUID id, String title, boolean done, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
    }

    static TaskEntity from(Task task) {
        return new TaskEntity(task.id(), task.title(), task.done(), task.createdAt());
    }

    Task toDomain() {
        return new Task(id, title, done, createdAt);
    }
}
