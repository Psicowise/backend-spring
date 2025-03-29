package com.example.psicowise_backend_spring.controller.common;

import com.example.psicowise_backend_spring.dto.common.CriarTelefoneDto;
import com.example.psicowise_backend_spring.dto.common.TelefoneDto;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
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
     * Lista todos os telefones de um proprietário
     *
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     * @return Lista de telefones
     */
    @GetMapping("/proprietario/{proprietarioId}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorProprietario(
            @PathVariable UUID proprietarioId,
            @RequestParam TipoProprietario tipoProprietario) {
        return telefoneService.listarTelefonesPorProprietario(proprietarioId, tipoProprietario);
    }

    /**
     * Obtém o telefone WhatsApp de um proprietário
     *
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     * @return O telefone WhatsApp formatado ou null
     */
    @GetMapping("/whatsapp/{proprietarioId}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<String> buscarWhatsappDoProprietario(
            @PathVariable UUID proprietarioId,
            @RequestParam TipoProprietario tipoProprietario) {
        String telefone = telefoneService.obterTelefoneWhatsapp(proprietarioId, tipoProprietario);
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
     * API de compatibilidade para buscar telefones de pacientes (para manter compatibilidade com código existente)
     *
     * @param pacienteId ID do paciente
     * @return Lista de telefones
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PSICOLOGO')")
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPaciente(@PathVariable UUID pacienteId) {
        return telefoneService.listarTelefonesPorProprietario(pacienteId, TipoProprietario.PACIENTE);
    }

    /**
     * API de compatibilidade para buscar telefones de psicólogos (para manter compatibilidade com código existente)
     *
     * @param psicologoId ID do psicólogo
     * @return Lista de telefones
     */
    @GetMapping("/psicologo/{psicologoId}")
    @PreAuthorize("hasAnyRole('PSICOLOGO', 'ADMIN')")
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPsicologo(@PathVariable UUID psicologoId) {
        return telefoneService.listarTelefonesPorProprietario(psicologoId, TipoProprietario.PSICOLOGO);
    }
}