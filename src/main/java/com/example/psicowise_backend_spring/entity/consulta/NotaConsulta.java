
package com.example.psicowise_backend_spring.entity.consulta;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "TB_NOTAS_CONSULTA")
@EntityListeners(AuditingEntityListener.class)
public class NotaConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataNota;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Métodos que estavam faltando
    public UUID getId() {
        return id;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public void setDataNota(LocalDateTime dataNota) {
        this.dataNota = dataNota;
    }
}
