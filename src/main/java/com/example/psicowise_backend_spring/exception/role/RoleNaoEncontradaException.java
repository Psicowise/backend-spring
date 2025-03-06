package com.example.psicowise_backend_spring.exception.role;

public class RoleNaoEncontradaException extends RoleException {
    public RoleNaoEncontradaException(String identificador) {
        super("Role não encontrada com o identificador: " + identificador);
    }
}
