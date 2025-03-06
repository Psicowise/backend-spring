package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.enums.consulta.StatusFaturamentoPlano;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_FATURAMENTOS_PLANO_SAUDE")
@EntityListeners(AuditingEntityListener.class)
public class FaturamentoPlanoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Column(name = "nome_plano")
    private String nomePlano;

    @Column(name = "numero_autorizacao")
    private String numeroAutorizacao;

    @Column(name = "valor")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusFaturamentoPlano status;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "observacoes")
    private String observacoes;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}