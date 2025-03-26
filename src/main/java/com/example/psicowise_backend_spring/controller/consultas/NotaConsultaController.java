package com.example.psicowise_backend_spring.controller.consultas;

import com.example.psicowise_backend_spring.dto.consultas.CriarNotaConsultaDto;
import com.example.psicowise_backend_spring.dto.consultas.NotaConsultaDto;
import com.example.psicowise_backend_spring.service.consultas.NotaConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notas-consulta")
@PreAuthorize("hasRole('PSICOLOGO')")
public class NotaConsultaController {

    private final NotaConsultaService notaConsultaService;

    @Autowired
    public NotaConsultaController(NotaConsultaService notaConsultaService) {
        this.notaConsultaService = notaConsultaService;
    }

    /**
     * Cria uma nova nota
     * @param notaDto DTO com os dados da nota
     * @return A nota criada
     */
    @PostMapping
    public ResponseEntity<NotaConsultaDto> criarNota(@RequestBody CriarNotaConsultaDto notaDto) {
        return notaConsultaService.criarNota(notaDto);
    }

    /**
     * Atualiza uma nota existente
     * @param id ID da nota
     * @param notaDto DTO com os novos dados
     * @return A nota atualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotaConsultaDto> atualizarNota(@PathVariable UUID id, @RequestBody CriarNotaConsultaDto notaDto) {
        return notaConsultaService.atualizarNota(id, notaDto);
    }

    /**
     * Busca uma nota pelo ID
     * @param id ID da nota
     * @return A nota encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotaConsultaDto> buscarNotaPorId(@PathVariable UUID id) {
        return notaConsultaService.buscarNotaPorId(id);
    }

    /**
     * Lista todas as notas de uma consulta
     * @param consultaId ID da consulta
     * @return Lista de notas
     */
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<List<NotaConsultaDto>> listarNotasPorConsulta(@PathVariable UUID consultaId) {
        return notaConsultaService.listarNotasPorConsulta(consultaId);
    }

    /**
     * Lista todas as notas de um paciente
     * @param pacienteId ID do paciente
     * @return Lista de notas
     */
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<NotaConsultaDto>> listarNotasPorPaciente(@PathVariable UUID pacienteId) {
        return notaConsultaService.listarNotasPorPaciente(pacienteId);
    }

    /**
     * Exclui uma nota
     * @param id ID da nota
     * @return Mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> excluirNota(@PathVariable UUID id) {
        return notaConsultaService.excluirNota(id);
    }
}