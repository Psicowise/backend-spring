package com.example.psicowise_backend_spring.dto.autenticacao;

public record LoginRequestDto(
    String email,
    String senha
) {}
