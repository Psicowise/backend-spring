package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalaVideoRepository extends JpaRepository<SalaVideo, UUID> {

    Optional<SalaVideo> findByConsulta(Consulta consulta);
    Optional<SalaVideo> findByConsultaId(UUID consultaId);
    Optional<SalaVideo> findBySalaId(String salaId);
    List<SalaVideo> findByAtiva(boolean ativa);
    List<SalaVideo> findByDataAtivacaoBetween(LocalDateTime inicio, LocalDateTime fim);
}