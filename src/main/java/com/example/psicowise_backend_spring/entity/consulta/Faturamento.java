package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_FATURAMENTOS")
public class Faturamento {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento")
    private TipoPagamento tipoPagamento;

    @Getter
    @Setter
    @Column(name = "valor")
    private BigDecimal valor;

    @Getter
    @Setter
    @Column(name = "pago")
    private boolean pago;

    @Getter
    @Setter
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Getter
    @Setter
    @Column(name = "observacoes")
    private String observacoes;

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
