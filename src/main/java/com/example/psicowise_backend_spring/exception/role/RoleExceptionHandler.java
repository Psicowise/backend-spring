package com.example.psicowise_backend_spring.exception.role;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class RoleExceptionHandler {

    public ResponseEntity<Object> handleRoleJaExisteException(RoleJaExisteException ex) {
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.CONFLICT);
    }

    public ResponseEntity<Object> handleRoleNaoEncontradaException(RoleNaoEncontradaException ex) {
        return criarRespostaDeErro(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Object> criarRespostaDeErro(String mensagem, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensagem);

        return new ResponseEntity<>(body, status);
    }
}
