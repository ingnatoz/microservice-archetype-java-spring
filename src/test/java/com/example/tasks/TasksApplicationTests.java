package com.example.tasks;

import static org.assertj.core.api.Assertions.assertThat;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class TasksApplicationTests {

    @Autowired
    private MockMvcTester mvc;

    @Test
    void crudRoundTrip() throws Exception {
        MvcTestResult created = mvc.post()
                .uri("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Buy bread\"}")
                .exchange();
        assertThat(created).hasStatus(HttpStatus.CREATED);
        String location = created.getResponse().getHeader(HttpHeaders.LOCATION);
        String createdAt = JsonPath.read(created.getResponse().getContentAsString(), "$.createdAt");

        assertThat(mvc.get().uri(location))
                .bodyJson()
                .extractingPath("$.createdAt")
                .isEqualTo(createdAt);

        assertThat(mvc.put()
                        .uri(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Buy whole wheat bread\", \"done\": true}"))
                .hasStatusOk();
        assertThat(mvc.get().uri("/api/v1/tasks"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].title")
                .isEqualTo("Buy whole wheat bread");

        assertThat(mvc.delete().uri(location)).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(mvc.get().uri(location)).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void actuatorExposesOnlyHealth() {
        assertThat(mvc.get().uri("/actuator/health"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.status")
                .isEqualTo("UP");
        assertThat(mvc.get().uri("/actuator/env")).hasStatus(HttpStatus.NOT_FOUND);
    }
}
