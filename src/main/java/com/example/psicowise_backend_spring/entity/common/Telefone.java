package com.example.psicowise_backend_spring.entity.common;

import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
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

    // Novo campo para armazenar o ID do proprietário (seja paciente, psicólogo ou outro)
    @Column(name = "proprietario_id", nullable = false)
    private UUID proprietarioId;

    // Novo campo para indicar o tipo do proprietário
    @Column(name = "tipo_proprietario", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoProprietario tipoProprietario;

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

    /**
     * Retorna o número formatado de maneira legível
     * Formato: (DDD) NUMERO
     */
    @Transient
    public String getNumeroFormatado() {
        return "(" + ddd + ") " + numero;
    }
}