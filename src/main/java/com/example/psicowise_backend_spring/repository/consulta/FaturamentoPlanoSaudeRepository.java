package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.FaturamentoPlanoSaude;
import com.example.psicowise_backend_spring.enums.consulta.StatusFaturamentoPlano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FaturamentoPlanoSaudeRepository extends JpaRepository<FaturamentoPlanoSaude, UUID> {

    List<FaturamentoPlanoSaude> findByConsulta(Consulta consulta);
    List<FaturamentoPlanoSaude> findByConsultaId(UUID consultaId);
    List<FaturamentoPlanoSaude> findByNomePlano(String nomePlano);
    List<FaturamentoPlanoSaude> findByStatus(StatusFaturamentoPlano status);
    List<FaturamentoPlanoSaude> findByDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);
    List<FaturamentoPlanoSaude> findByDataPagamentoBetween(LocalDateTime inicio, LocalDateTime fim);
}
