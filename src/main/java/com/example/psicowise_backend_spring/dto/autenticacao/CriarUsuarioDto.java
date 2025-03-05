package com.example.psicowise_backend_spring.dto.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;

public record CriarUsuarioDto(
        String nome,
        String sobrenome,
        String email,
        String senha,
        String role
) {}
