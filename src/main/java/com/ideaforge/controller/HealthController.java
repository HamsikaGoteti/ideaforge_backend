package com.ideaforge.controller;

import com.ideaforge.entity.IdeaMode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("app", "IdeaForge");
        health.put("version", "1.0.0");
        health.put("timestamp", LocalDateTime.now().toString());

        boolean dbConnected = false;
        String dbProduct = "Unknown";
        try (Connection conn = dataSource.getConnection()) {
            dbConnected = !conn.isClosed();
            dbProduct = conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion();
        } catch (Exception e) {
            health.put("databaseError", e.getMessage());
        }

        health.put("databaseConnected", dbConnected);
        health.put("databaseProduct", dbProduct);
        health.put("supportedModes", Arrays.stream(IdeaMode.values()).map(IdeaMode::name).collect(Collectors.toList()));

        return ResponseEntity.ok(health);
    }
}
