package com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_DOACAO")
public class Doacao {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "configuracao_pagamento_id")
    private ConfiguracaoPagamento configuracaoPagamento;

    @Getter
    @Setter
    @Column(name = "valor_sugerido")
    private BigDecimal valorSugerido;

    @Getter
    @Setter
    @Column(name = "observacao")
    private String observacao;

    @Getter
    @Setter
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
