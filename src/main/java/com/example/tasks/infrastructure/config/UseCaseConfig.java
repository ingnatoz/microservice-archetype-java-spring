package com.example.tasks.infrastructure.config;

import com.example.tasks.application.TaskRepository;
import com.example.tasks.application.TaskService;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class UseCaseConfig {

    private static final Duration DATABASE_TIMESTAMP_PRECISION = Duration.of(1, ChronoUnit.MICROS);

    @Bean
    TaskService taskService(TaskRepository taskRepository) {
        return new TaskService(taskRepository, Clock.tick(Clock.systemUTC(), DATABASE_TIMESTAMP_PRECISION));
    }
}
