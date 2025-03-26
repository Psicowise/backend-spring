package com.example.psicowise_backend_spring.entity.consulta;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "TB_SALAS_VIDEO")
@EntityListeners(AuditingEntityListener.class)
public class SalaVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @Column(name = "sala_id", nullable = false, unique = true)
    private String salaId;

    @Column(name = "link_acesso", nullable = false)
    private String linkAcesso;

    @Column(name = "link_host", nullable = false)
    private String linkHost;

    @Column(name = "ativa")
    private boolean ativa;

    @Column(name = "data_ativacao")
    private LocalDateTime dataAtivacao;

    @Column(name = "data_desativacao")
    private LocalDateTime dataDesativacao;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}