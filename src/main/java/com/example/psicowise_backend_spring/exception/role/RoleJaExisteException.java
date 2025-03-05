package com.example.psicowise_backend_spring.exception.role;

public class RoleJaExisteException extends RoleException {
    public RoleJaExisteException(String role) {
        super("Role '" + role + "' já existe");
    }
}
