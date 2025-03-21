package com.example.psicowise_backend_spring.dto.consultas;

public record AtualizarPsicologoDto(
        String nome,
        String sobrenome,
        String email,
        String especialidade,
        String crp
) {}
