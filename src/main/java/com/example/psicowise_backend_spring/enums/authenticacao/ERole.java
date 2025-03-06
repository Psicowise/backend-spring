package com.example.psicowise_backend_spring.enums.authenticacao;

public enum ERole {
    ADMIN("ROLE_ADMIN"), 
    USER("ROLE_USER"),
    PSICOLOGO("ROLE_PSICOLOGO");

    private final String role;

    ERole(String role) {
        this.role = role;
    }
}
