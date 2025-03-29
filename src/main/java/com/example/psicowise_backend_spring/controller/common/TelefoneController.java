package com.example.psicowise_backend_spring.controller.common;

import com.example.psicowise_backend_spring.dto.common.CriarTelefoneDto;
import com.example.psicowise_backend_spring.dto.common.TelefoneDto;
import com.example.psicowise_backend_spring.service.common.TelefoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/telefones")
@RequiredArgsConstructor
public class TelefoneController {

    private final TelefoneService telefoneService;

    /**
     * Cria um novo telefone
     *
     * @param telefoneDto DTO com os dados do telefone
     * @return O telefone criado
     */
    @PostMapping
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<TelefoneDto> criarTelefone(@RequestBody CriarTelefoneDto telefoneDto) {
        return telefoneService.criarTelefone(telefoneDto);
    }

    /**
     * Atualiza um telefone existente
     *
     * @param id ID do telefone
     * @param telefoneDto DTO com os novos dados
     * @return O telefone atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<TelefoneDto> atualizarTelefone(
            @PathVariable UUID id,
            @RequestBody CriarTelefoneDto telefoneDto) {
        return telefoneService.atualizarTelefone(id, telefoneDto);
    }

    /**
     * Busca um telefone pelo ID
     *
     * @param id ID do telefone
     * @return O telefone encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<TelefoneDto> buscarTelefonePorId(@PathVariable UUID id) {
        return telefoneService.buscarTelefonePorId(id);
    }


    /**
     * Busca um telefone pelo ID do paciente
     *
     * @param pacienteId ID do paciente
     * @return O telefone encontrado
     */
    @GetMapping("/paciente/whatsapp/{pacienteId}")
    public ResponseEntity<String> buscarWhatsappDoPaciente(@PathVariable UUID pacienteId) {
        String telefone = telefoneService.obterTelefoneWhatsappPaciente(pacienteId);
        return ResponseEntity.ok(telefone);
    }

    /**
     * Exclui um telefone
     *
     * @param id ID do telefone
     * @return Mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<String> excluirTelefone(@PathVariable UUID id) {
        return telefoneService.excluirTelefone(id);
    }

    /**
     * Lista todos os telefones de um paciente
     *
     * @param pacienteId ID do paciente
     * @return Lista de telefones
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPaciente(@PathVariable UUID pacienteId) {
        return telefoneService.listarTelefonesPorPaciente(pacienteId);
    }

    /**
     * Lista todos os telefones de um psicólogo
     *
     * @return Lista de telefones
     */
    @GetMapping("/psicologo/")
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'ADMIN')")
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPsicologo() {
        return telefoneService.listarTelefonesPorPsicologo();
    }
}
