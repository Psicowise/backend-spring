package com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PagamentoAvulso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoAvulsoRepository extends JpaRepository<PagamentoAvulso, UUID> {

    Optional<PagamentoAvulso> findByConfiguracaoPagamento(ConfiguracaoPagamento configuracaoPagamento);
    Optional<PagamentoAvulso> findByConfiguracaoPagamentoId(UUID configuracaoPagamentoId);
}
