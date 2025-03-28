package com.example.psicowise_backend_spring.dto.common;

import com.example.psicowise_backend_spring.enums.common.TipoTelefone;

import java.util.UUID;

public record TelefoneDto(
        UUID id,
        String numero,
        String ddd,
        String codigoPais,
        TipoTelefone tipo,
        boolean principal,
        boolean whatsapp,
        String observacao
) {}
