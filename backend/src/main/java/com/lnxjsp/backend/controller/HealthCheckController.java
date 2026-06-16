package com.lnxjsp.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");

        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                status.put("database", "CONNECTED");
                return ResponseEntity.ok(status);
            }
        } catch (SQLException e) {
            status.put("database", "DISCONNECTED");
            status.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
        }

        status.put("database", "DISCONNECTED");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(status);
    }
}
