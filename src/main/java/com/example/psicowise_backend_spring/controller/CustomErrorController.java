package com.example.psicowise_backend_spring.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<?> handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode != null) {
            int status = Integer.parseInt(statusCode.toString());

            if (status == HttpStatus.NOT_FOUND.value()) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "Not Found");
                body.put("status", 404);
                body.put("message", "O recurso requisitado não existe.");
                return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
            }

            if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                Map<String, Object> body = new HashMap<>();
                body.put("error", "Internal Server Error");
                body.put("status", 500);
                body.put("message", "Erro interno do servidor.");
                return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

