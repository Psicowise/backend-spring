package com.example.psicowise_backend_spring.dto.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;

import java.util.UUID;

public record RoleDto(UUID id, String role) {
    public static RoleDto fromEntity(Role role) {
        return new RoleDto(role.getId(), role.getRole().name());
    }
}
