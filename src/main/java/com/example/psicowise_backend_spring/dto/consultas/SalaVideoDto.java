package com.example.psicowise_backend_spring.dto.consultas;

import java.time.LocalDateTime;
import java.util.UUID;

public record SalaVideoDto(
        UUID id,
        UUID consultaId,
        String salaId,
        String linkAcesso,
        String linkHost,
        boolean ativa,
        LocalDateTime dataAtivacao,
        LocalDateTime dataDesativacao
) {}