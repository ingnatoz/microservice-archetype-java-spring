package com.example.tasks.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.validation.BindValidationException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class CorsConfigTest {

    @Test
    void wildcardOriginStopsTheStartup() {
        new ApplicationContextRunner()
                .withUserConfiguration(CorsConfig.class)
                .withPropertyValues("app.cors.allowed-origins=*")
                .run(context -> assertThat(context).getFailure().hasRootCauseInstanceOf(BindValidationException.class));
    }
}
