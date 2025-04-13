package com.example.psicowise_backend_spring.dto.autenticacao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CriarUsuarioDto(
        String nome,
        String sobrenome,
        String email,
        String senha
) {}
