package com.example.psicowise_backend_spring.dto.autenticacao;

import java.util.List;
import java.util.UUID;

public record LoginResponseDto(
        String token,
        UUID id,
        String nome,
        String sobrenome,
        String email,
        List<String> roles) {}
