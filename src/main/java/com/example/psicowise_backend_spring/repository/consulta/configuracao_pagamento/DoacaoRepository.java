package com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento;

import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.Doacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoacaoRepository extends JpaRepository<Doacao, UUID> {

    Optional<Doacao> findByConfiguracaoPagamento(ConfiguracaoPagamento configuracaoPagamento);
    Optional<Doacao> findByConfiguracaoPagamentoId(UUID configuracaoPagamentoId);
}
