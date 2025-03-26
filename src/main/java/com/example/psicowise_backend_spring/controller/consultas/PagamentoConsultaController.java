package com.example.psicowise_backend_spring.controller.consultas;

import com.example.psicowise_backend_spring.dto.consultas.EditarPagamentoConsultaDto;
import com.example.psicowise_backend_spring.service.consultas.PagamentoConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagamentos-consulta")
@PreAuthorize("hasRole('PSICOLOGO')")
public class PagamentoConsultaController {

    private final PagamentoConsultaService pagamentoConsultaService;

    @Autowired
    public PagamentoConsultaController(PagamentoConsultaService pagamentoConsultaService) {
        this.pagamentoConsultaService = pagamentoConsultaService;
    }

    /**
     * Edita a forma de pagamento de uma consulta
     *
     * @param pagamentoDto DTO com os dados do pagamento
     * @return Mensagem de sucesso ou erro
     */
    @PutMapping
    public ResponseEntity<String> editarPagamentoConsulta(@RequestBody EditarPagamentoConsultaDto pagamentoDto) {
        return pagamentoConsultaService.editarPagamentoConsulta(pagamentoDto);
    }

    /**
     * Obtém os detalhes de pagamento de uma consulta
     *
     * @param consultaId ID da consulta
     * @return Dados do pagamento
     */
    @GetMapping("/{consultaId}")
    public ResponseEntity<Object> obterPagamentoConsulta(@PathVariable UUID consultaId) {
        return pagamentoConsultaService.obterPagamentoConsulta(consultaId);
    }

    /**
     * Registra o recebimento de um pagamento
     *
     * @param consultaId ID da consulta
     * @param payload Contém as observações sobre o pagamento
     * @return Mensagem de sucesso ou erro
     */
    @PostMapping("/{consultaId}/receber")
    public ResponseEntity<String> registrarPagamentoRecebido(
            @PathVariable UUID consultaId,
            @RequestBody Map<String, String> payload) {
        String observacoes = payload.get("observacoes");
        return pagamentoConsultaService.registrarPagamentoRecebido(consultaId, observacoes);
    }

    /**
     * Cancela um faturamento
     *
     * @param consultaId ID da consulta
     * @param payload Contém o motivo do cancelamento
     * @return Mensagem de sucesso ou erro
     */
    @PostMapping("/{consultaId}/cancelar")
    public ResponseEntity<String> cancelarFaturamento(
            @PathVariable UUID consultaId,
            @RequestBody Map<String, String> payload) {
        String motivo = payload.get("motivo");
        return pagamentoConsultaService.cancelarFaturamento(consultaId, motivo);
    }
}