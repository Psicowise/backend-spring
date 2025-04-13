package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.usuario.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Consulta> findByPsicologoAndDataHoraBetween(Psicologo psicologo, LocalDateTime inicio, LocalDateTime fim);
}