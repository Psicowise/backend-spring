package com.example.psicowise_backend_spring.dto.consultas;

import java.util.UUID;

public record PsicologoDto(
        UUID id,
        String nome,
        String sobrenome,
        String email,
        String crp,
        String especialidade
        ) {}
