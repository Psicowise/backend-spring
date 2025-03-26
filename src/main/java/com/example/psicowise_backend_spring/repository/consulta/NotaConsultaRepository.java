package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.NotaConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotaConsultaRepository extends JpaRepository<NotaConsulta, UUID> {

    List<NotaConsulta> findByConsulta(Consulta consulta);
    List<NotaConsulta> findByConsultaId(UUID consultaId);
    List<NotaConsulta> findByConsultaIdOrderByDataNotaDesc(UUID consultaId);
    List<NotaConsulta> findByDataNotaBetween(LocalDateTime inicio, LocalDateTime fim);

    // Método para buscar notas por paciente
    @Query("SELECT n FROM NotaConsulta n WHERE n.consulta.paciente.id = :pacienteId ORDER BY n.dataNota DESC")
    List<NotaConsulta> findByPacienteIdOrderByDataNotaDesc(@Param("pacienteId") UUID pacienteId);
}