package com.example.psicowise_backend_spring.entity.common;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.common.TipoTelefone;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_TELEFONES")
@EntityListeners(AuditingEntityListener.class)
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "ddd", nullable = false)
    private String ddd;

    @Column(name = "codigo_pais", nullable = false)
    private String codigoPais;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoTelefone tipo;

    @Column(name = "principal")
    private boolean principal;

    @Column(name = "whatsapp")
    private boolean whatsapp;

    @Column(name = "observacao")
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Retorna o número formatado para envio de mensagens WhatsApp
     * Formato: codigoPais + ddd + numero (sem espaços ou caracteres especiais)
     */
    @Transient
    public String getNumeroFormatadoWhatsapp() {
        return codigoPais + ddd + numero.replaceAll("[^0-9]", "");
    }
}
