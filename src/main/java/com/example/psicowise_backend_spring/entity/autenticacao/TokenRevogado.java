package com.example.psicowise_backend_spring.entity.autenticacao;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "TB_TOKENS_REVOGADOS")
public class TokenRevogado {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", length = 2000, nullable = false, unique = true)
    private String token;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(name = "expiracao")
    private LocalDateTime expiracao;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
