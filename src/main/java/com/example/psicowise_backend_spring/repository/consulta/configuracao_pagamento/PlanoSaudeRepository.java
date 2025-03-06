package com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.PlanoSaude;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlanoSaudeRepository extends JpaRepository<PlanoSaude, UUID> {

    Optional<PlanoSaude> findByConfiguracaoPagamento(ConfiguracaoPagamento configuracaoPagamento);
    Optional<PlanoSaude> findByConfiguracaoPagamentoId(UUID configuracaoPagamentoId);
    List<PlanoSaude> findByNomePlano(String nomePlano);
}
