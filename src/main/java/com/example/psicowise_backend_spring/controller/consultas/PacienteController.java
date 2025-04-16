
package com.example.psicowise_backend_spring.controller.consultas;

import com.example.psicowise_backend_spring.dto.paciente.PacienteRequestDTO;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.service.consultas.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        return ResponseEntity.ok(pacienteService.listarTodos());
    }

    @GetMapping("/psicologo")
    public ResponseEntity<List<Paciente>> buscarPorPsicologoLogado() {
        return ResponseEntity.ok(pacienteService.buscarPorPsicologoLogado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable UUID id) {
        return pacienteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paciente> criar(@RequestBody PacienteRequestDTO pacienteDTO) {
        return ResponseEntity.ok(pacienteService.criar(pacienteDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(@PathVariable UUID id, @RequestBody PacienteRequestDTO pacienteDTO) {
        return ResponseEntity.ok(pacienteService.atualizar(id, pacienteDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        pacienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
