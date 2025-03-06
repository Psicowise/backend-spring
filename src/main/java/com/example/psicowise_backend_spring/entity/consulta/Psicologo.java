package com.example.psicowise_backend_spring.entity.consulta;

import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "TB_PSICOLOGOS")
public class Psicologo extends Usuario {

    @Column(name = "crp", unique = true)
    private String crp;

    @Column(name = "especialidade")
    private String especialidade;

    @OneToMany(mappedBy = "psicologo")
    private List<Paciente> pacientes;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}