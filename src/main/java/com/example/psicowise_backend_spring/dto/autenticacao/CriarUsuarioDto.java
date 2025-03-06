package com.example.psicowise_backend_spring.dto.autenticacao;


public record CriarUsuarioDto(
        String nome,
        String sobrenome,
        String email,
        String senha
) {}
