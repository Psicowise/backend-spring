package com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PagamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoMensalRepository extends JpaRepository<PagamentoMensal, UUID> {

    Optional<PagamentoMensal> findByConfiguracaoPagamento(ConfiguracaoPagamento configuracaoPagamento);
    Optional<PagamentoMensal> findByConfiguracaoPagamentoId(UUID configuracaoPagamentoId);
    List<PagamentoMensal> findByDiaVencimento(Integer diaVencimento);
}
