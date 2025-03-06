package com.example.psicowise_backend_spring.exception.usuario;

public class RoleNaoEncontradaException extends UsuarioException {
    public RoleNaoEncontradaException(String role) {
        super("Role '" + role + "' não encontrada");
    }
}