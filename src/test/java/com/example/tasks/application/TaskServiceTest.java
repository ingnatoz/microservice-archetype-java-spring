package com.example.tasks.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.tasks.domain.Task;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TaskServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    private final TaskRepository repository = mock(TaskRepository.class);
    private final TaskService service = new TaskService(repository, Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void createSavesTaskWithClockTime() {
        Task task = service.create("Buy bread");

        assertThat(task.createdAt()).isEqualTo(NOW);
        verify(repository).save(task);
    }

    @Test
    void updateSavesTheChanges() {
        Task existing = Task.create("Buy bread", NOW);
        when(repository.findById(existing.id())).thenReturn(Optional.of(existing));

        Task updated = service.update(existing.id(), "Buy milk", true);

        assertThat(updated.done()).isTrue();
        verify(repository).save(updated);
    }

    @Test
    void getFailsWhenTaskDoesNotExist() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(UUID.randomUUID())).isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void deleteFailsWhenTaskDoesNotExist() {
        when(repository.deleteById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.delete(UUID.randomUUID())).isInstanceOf(TaskNotFoundException.class);
    }
}
