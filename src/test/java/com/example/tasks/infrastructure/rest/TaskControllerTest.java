package com.example.tasks.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.tasks.application.TaskNotFoundException;
import com.example.tasks.application.TaskService;
import com.example.tasks.domain.InvalidTaskException;
import com.example.tasks.domain.Task;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final Task TASK =
            new Task(UUID.randomUUID(), "Buy bread", false, Instant.parse("2026-01-01T10:00:00Z"));

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private TaskService service;

    @Test
    void createReturns201WithLocation() {
        when(service.create("Buy bread")).thenReturn(TASK);

        assertThat(mvc.post()
                        .uri("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Buy bread\"}"))
                .hasStatus(HttpStatus.CREATED)
                .hasHeader(HttpHeaders.LOCATION, "/api/v1/tasks/" + TASK.id())
                .bodyJson()
                .extractingPath("$.createdAt")
                .isEqualTo("2026-01-01T10:00:00Z");
    }

    @Test
    void invalidBodyReturnsProblemWithFieldErrors() {
        assertThat(mvc.post()
                        .uri("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \" \"}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON)
                .bodyJson()
                .extractingPath("$.errors.title")
                .isEqualTo("Title is required");
    }

    @Test
    void domainRuleViolationReturns400() {
        when(service.create(any())).thenThrow(new InvalidTaskException("Domain rule"));

        assertThat(mvc.post()
                        .uri("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Buy bread\"}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.detail")
                .isEqualTo("Domain rule");
    }

    @Test
    void missingTaskReturns404Problem() {
        when(service.get(TASK.id())).thenThrow(new TaskNotFoundException(TASK.id()));

        assertThat(mvc.get().uri("/api/v1/tasks/{id}", TASK.id()))
                .hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void pageSizeAbove100Returns400() {
        assertThat(mvc.get().uri("/api/v1/tasks?size=101")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void unexpectedErrorDoesNotLeakItsMessage() {
        when(service.list(0, 20)).thenThrow(new IllegalStateException("SELECT * FROM tasks"));

        assertThat(mvc.get().uri("/api/v1/tasks"))
                .hasStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyText()
                .doesNotContain("SELECT");
    }

    @Test
    void corsAllowsConfiguredOrigin() {
        assertThat(mvc.options()
                        .uri("/api/v1/tasks")
                        .header(HttpHeaders.ORIGIN, "http://localhost:8080")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8080");
    }

    @Test
    void corsRejectsUnknownOrigin() {
        assertThat(mvc.options()
                        .uri("/api/v1/tasks")
                        .header(HttpHeaders.ORIGIN, "https://other.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .hasStatus(HttpStatus.FORBIDDEN);
    }
}
