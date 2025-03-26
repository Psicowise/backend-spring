package com.example.psicowise_backend_spring.controller.consultas;

import com.example.psicowise_backend_spring.dto.consultas.SalaVideoDto;
import com.example.psicowise_backend_spring.service.consultas.SalaVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/salas-video")
public class SalaVideoController {

    private final SalaVideoService salaVideoService;

    @Autowired
    public SalaVideoController(SalaVideoService salaVideoService) {
        this.salaVideoService = salaVideoService;
    }

    /**
     * Cria uma sala de vídeo para uma consulta
     *
     * @param consultaId ID da consulta
     * @return DTO com os dados da sala criada
     */
    @PostMapping("/consulta/{consultaId}")
    public ResponseEntity<SalaVideoDto> criarSalaVideo(@PathVariable UUID consultaId) {
        return salaVideoService.criarSalaVideo(consultaId);
    }

    /**
     * Ativa uma sala de vídeo
     *
     * @param salaId ID da sala
     * @return Mensagem de sucesso ou erro
     */
    @PostMapping("/{salaId}/ativar")
    public ResponseEntity<String> ativarSala(@PathVariable UUID salaId) {
        return salaVideoService.ativarSala(salaId);
    }

    /**
     * Desativa uma sala de vídeo
     *
     * @param salaId ID da sala
     * @return Mensagem de sucesso ou erro
     */
    @PostMapping("/{salaId}/desativar")
    public ResponseEntity<String> desativarSala(@PathVariable UUID salaId) {
        return salaVideoService.desativarSala(salaId);
    }

    /**
     * Obtém informações da sala de vídeo de uma consulta
     *
     * @param consultaId ID da consulta
     * @return DTO com os dados da sala
     */
    @GetMapping("/consulta/{consultaId}")
    public ResponseEntity<SalaVideoDto> obterSalaVideo(@PathVariable UUID consultaId) {
        return salaVideoService.obterSalaVideo(consultaId);
    }
}