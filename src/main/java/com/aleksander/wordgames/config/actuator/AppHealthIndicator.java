package com.aleksander.wordgames.config.actuator;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class AppHealthIndicator implements HealthIndicator {

    private final long startedAt = System.currentTimeMillis();

    @Value("${spring.application.name}")
    private String appName;

    @Value("${app.version}")
    private String version;

    @Value("${spring.profiles.active:default}")
    private String profile;

    @Override
    public Health health() {

        long uptimeSeconds = (System.currentTimeMillis() - startedAt) / 1000;

        return Health.up()
                .withDetail("appName", appName)
                .withDetail("version", version)
                .withDetail("profile", profile)
                .withDetail("javaVersion", System.getProperty("java.version"))
                .withDetail("uptimeSeconds", uptimeSeconds)
                .withDetail("fetchedAt", Instant.now())
                .build();
    }
}