package com.example.tasks.infrastructure.rest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CorsConfig.Properties.class)
class CorsConfig implements WebMvcConfigurer {

    private final Properties properties;

    CorsConfig(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(properties.allowedOrigins().toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }

    @Validated
    @ConfigurationProperties("app.cors")
    record Properties(@NotEmpty List<@Pattern(regexp = "https?://[^*/\\s]+") String> allowedOrigins) {}
}
