package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Faturamento;
import com.example.psicowise_backend_spring.enums.consulta.TipoPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface FaturamentoRepository extends JpaRepository<Faturamento, UUID> {

    List<Faturamento> findByConsulta(Consulta consulta);
    List<Faturamento> findByConsultaId(UUID consultaId);
    List<Faturamento> findByTipoPagamento(TipoPagamento tipoPagamento);
    List<Faturamento> findByPago(boolean pago);
    List<Faturamento> findByDataPagamentoBetween(LocalDateTime inicio, LocalDateTime fim);
}
