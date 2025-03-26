package com.example.psicowise_backend_spring.dto.consultas;

import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import java.math.BigDecimal;
import java.util.UUID;

public record EditarPagamentoConsultaDto(
        UUID consultaId,
        TipoPagamento tipoPagamento,
        BigDecimal valorConsulta,

        // Campos para Plano de Saúde
        String nomePlano,
        String numeroAutorizacao,

        // Campos para Pagamento Mensal
        Integer sessoesIncluidas,
        Integer diaVencimento,

        // Campo para Doação
        String observacao
) {}