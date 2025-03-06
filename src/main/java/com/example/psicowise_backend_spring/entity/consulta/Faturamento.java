package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_FATURAMENTOS")
public class Faturamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento")
    private TipoPagamento tipoPagamento;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "pago")
    private boolean pago;

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
