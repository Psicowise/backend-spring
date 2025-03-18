package com.example.psicowise_backend_spring.controller.autenticacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("version", "1.0"); // Você pode adicionar a versão atual
        status.put("timestamp", new Date().toString());
        return ResponseEntity.ok(status);
    }
}
