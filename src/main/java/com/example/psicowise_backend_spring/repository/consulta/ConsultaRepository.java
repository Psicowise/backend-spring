package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.consulta.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {

    List<Consulta> findByPaciente(Paciente paciente);
    List<Consulta> findByPsicologo(Psicologo psicologo);
    List<Consulta> findByStatus(StatusConsulta status);
    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findByPsicologoAndDataHoraBetween(Psicologo psicologo, LocalDateTime inicio, LocalDateTime fim);
}
