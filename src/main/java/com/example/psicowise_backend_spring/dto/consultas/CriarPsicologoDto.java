package com.example.psicowise_backend_spring.dto.consultas;

import java.util.List;

public record CriarPsicologoDto(
        String crp,
        List<String> especialidade
) {}
