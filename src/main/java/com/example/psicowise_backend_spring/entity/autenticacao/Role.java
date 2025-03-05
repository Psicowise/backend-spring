package com.example.psicowise_backend_spring.entity.autenticacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_ROLES")
@EntityListeners(AuditingEntityListener.class)
public class Role {

    @Id
    @Getter
    @Setter
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @Column(name = "TB_ROLE")
    private String role;

    @CreatedDate
    @Getter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Getter
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
