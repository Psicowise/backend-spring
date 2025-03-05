package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_PSICOLOGOS")
public class Psicologo extends Usuario {

    @Getter
    @Setter
    @Column(name = "crp", unique = true)
    private String crp;

    @Getter
    @Setter
    @Column(name = "especialidade")
    private String especialidade;

    @Getter
    @Setter
    @OneToMany(mappedBy = "psicologo")
    private List<Paciente> pacientes;

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
