package com.example.psicowise_backend_spring.dto.consultas;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarNotaConsultaDto(
        UUID consultaId,
        String titulo,
        String conteudo,
        LocalDateTime dataNota
) {}