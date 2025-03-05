package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.enums.consulta.StatusConsulta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_CONSULTAS")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "psicologo_id")
    private Psicologo psicologo;

    @Getter
    @Setter
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Getter
    @Setter
    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusConsulta status;

    @Getter
    @Setter
    @OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL)
    private List<Faturamento> faturamentos;

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
