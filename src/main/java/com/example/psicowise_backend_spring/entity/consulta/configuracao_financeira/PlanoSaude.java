package com.example.psicowise_backend_spring.entity.consulta.configuracao_financeira;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.enums.consulta.StatusFaturamentoPlano;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_FATURAMENTOS_PLANO_SAUDE")
public class PlanoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Getter
    @Setter
    @Column(name = "nome_plano")
    private String nomePlano;

    @Getter
    @Setter
    @Column(name = "numero_autorizacao")
    private String numeroAutorizacao;

    @Getter
    @Setter
    @Column(name = "valor")
    private BigDecimal valor;

    @Getter
    @Setter
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusFaturamentoPlano status;

    @Getter
    @Setter
    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Getter
    @Setter
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "configuracao_pagamento_id")
    private ConfiguracaoPagamento configuracaoPagamento;

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
