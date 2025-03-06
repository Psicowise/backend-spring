package com.example.psicowise_backend_spring.repository.consulta.configuracao_pagamento;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira.ConfiguracaoPagamento;
import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfiguracaoPagamentoRepository extends JpaRepository<ConfiguracaoPagamento, UUID> {

    Optional<ConfiguracaoPagamento> findByPaciente(Paciente paciente);
    Optional<ConfiguracaoPagamento> findByPacienteId(UUID pacienteId);
    List<ConfiguracaoPagamento> findByTipoPagamento(TipoPagamento tipoPagamento);
}
