package com.example.tasks.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Task(UUID id, String title, boolean done, Instant createdAt) {

    public static final int TITLE_MAX_LENGTH = 120;

    public Task {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(createdAt, "createdAt");
        if (title == null || title.isBlank()) {
            throw new InvalidTaskException("Title is required");
        }
        if (title.length() > TITLE_MAX_LENGTH) {
            throw new InvalidTaskException("Title must be at most " + TITLE_MAX_LENGTH + " characters");
        }
    }

    public static Task create(String title, Instant now) {
        return new Task(UUID.randomUUID(), title, false, now);
    }

    public Task update(String newTitle, boolean newDone) {
        return new Task(id, newTitle, newDone, createdAt);
    }
}
