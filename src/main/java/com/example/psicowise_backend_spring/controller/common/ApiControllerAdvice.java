package com.example.psicowise_backend_spring.controller.common;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de exceções global que intercepta as exceções relacionadas a recursos não encontrados
 * e as trata de maneira adequada para endpoints de API
 */
@ControllerAdvice
@Order(1)
public class ApiControllerAdvice {

    /**
     * Manipula as exceções de recurso não encontrado, que podem ocorrer quando o Spring tenta
     * resolver um caminho como recurso estático em vez de endpoint de API
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", "Endpoint não encontrado: " + ex.getRequestURL());
        return error;
    }

    /**
     * Manipulador genérico para outras exceções que podem ocorrer
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "Um erro ocorreu: " + ex.getMessage());
        return error;
    }
}