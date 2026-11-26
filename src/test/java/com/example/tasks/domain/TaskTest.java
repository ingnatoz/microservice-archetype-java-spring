package com.example.tasks.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class TaskTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void newTaskIsPending() {
        Task task = Task.create("Buy bread", NOW);

        assertThat(task.done()).isFalse();
        assertThat(task.createdAt()).isEqualTo(NOW);
    }

    @Test
    void rejectsBlankTitle() {
        assertThatThrownBy(() -> Task.create(" ", NOW))
                .isInstanceOf(InvalidTaskException.class)
                .hasMessage("Title is required");
    }

    @Test
    void titleLengthLimitIsInclusive() {
        assertThat(Task.create("a".repeat(Task.TITLE_MAX_LENGTH), NOW).title()).hasSize(Task.TITLE_MAX_LENGTH);
        assertThatThrownBy(() -> Task.create("a".repeat(Task.TITLE_MAX_LENGTH + 1), NOW))
                .isInstanceOf(InvalidTaskException.class);
    }

    @Test
    void updateKeepsIdAndCreationDate() {
        Task task = Task.create("Buy bread", NOW);

        Task updated = task.update("Buy milk", true);

        assertThat(updated).isEqualTo(new Task(task.id(), "Buy milk", true, NOW));
    }
}
