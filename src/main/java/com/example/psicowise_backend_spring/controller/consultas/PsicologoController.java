package com.example.psicowise_backend_spring.controller.consultas;

import com.example.psicowise_backend_spring.dto.consultas.CriarPsicologoDto;
import com.example.psicowise_backend_spring.dto.consultas.PsicologoDto;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.service.consultas.PsicologoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/psicologos")
public class PsicologoController {

    private final PsicologoService psicologoService;

    @Autowired
    public PsicologoController(PsicologoService psicologoService) {
        this.psicologoService = psicologoService;
    }

    /**
     * Cria um novo psicólogo para o usuário autenticado
     * @param criarPsicologoDto DTO com os dados para criação do psicólogo
     * @return O psicólogo criado
     */
    @PostMapping
    public ResponseEntity<Psicologo> criarPsicologo(@RequestBody CriarPsicologoDto criarPsicologoDto) {
        return psicologoService.criarPsicologo(criarPsicologoDto);
    }

    /**
     * Busca um psicólogo pelo ID
     * @param id ID do psicólogo
     * @return O psicólogo encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Psicologo> buscarPsicologoPorId(@PathVariable UUID id) {
        // Você precisará implementar este método no serviço
        return ResponseEntity.ok().build();
    }

    /**
     * Lista todos os psicólogos
     * @return Lista de psicólogos
     */
    @GetMapping
    public ResponseEntity<List<Psicologo>> listarTodosPsicologos() {
        // Você precisará implementar este método no serviço
        return ResponseEntity.ok().build();
    }

    /**
     * Busca psicólogos por especialidade
     * @param especialidade Nome da especialidade
     * @return Lista de psicólogos com a especialidade especificada
     */
    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<Psicologo>> buscarPsicologosPorEspecialidade(@PathVariable String especialidade) {
        // Você precisará implementar este método no serviço
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza os dados de um psicólogo
     * @param id ID do psicólogo
     * @param criarPsicologoDto DTO com os novos dados do psicólogo
     * @return O psicólogo atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<Psicologo> atualizarPsicologo(@PathVariable UUID id, @RequestBody CriarPsicologoDto criarPsicologoDto) {
        // Você precisará implementar este método no serviço
        return ResponseEntity.ok().build();
    }

    /**
     * Remove um psicólogo
     * @param id ID do psicólogo
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPsicologo(@PathVariable UUID id) {
        // Você precisará implementar este método no serviço
        return ResponseEntity.noContent().build();
    }
}