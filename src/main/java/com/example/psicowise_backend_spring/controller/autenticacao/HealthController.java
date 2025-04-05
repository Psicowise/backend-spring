package com.example.psicowise_backend_spring.controller.autenticacao;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("version", "1.0");
        status.put("timestamp", new Date().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/api-check")
    public ResponseEntity<Map<String, Object>> apiCheck(HttpServletRequest request) {
        Map<String, Object> check = new HashMap<>();
        check.put("status", "API routes check OK");
        check.put("requestURI", request.getRequestURI());
        check.put("timestamp", new Date().toString());
        return ResponseEntity.ok(check);
    }
}
