package com.example.psicowise_backend_spring.entity.consulta;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "TB_SALA_VIDEO")
public class SalaVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    private String linkSala;
    private String tokenAcesso;
    private LocalDateTime validadeToken;
    private boolean ativa;
}
