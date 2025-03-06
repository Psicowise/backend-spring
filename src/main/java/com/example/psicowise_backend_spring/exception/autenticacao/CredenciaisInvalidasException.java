package com.example.psicowise_backend_spring.exception.autenticacao;

public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String message) {
        super(message);
    }
}