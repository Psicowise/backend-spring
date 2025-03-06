package com.example.psicowise_backend_spring.exception.role;

import com.example.psicowise_backend_spring.enums.authenticacao.ERole;

public class RoleJaExisteException extends RoleException {
    public RoleJaExisteException(ERole role) {
        super("Role '" + role + "' já existe");
    }
}
