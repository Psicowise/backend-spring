package com.example.psicowise_backend_spring.service.common;

import com.example.psicowise_backend_spring.dto.common.CriarTelefoneDto;
import com.example.psicowise_backend_spring.dto.common.TelefoneDto;
import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.repository.common.TelefoneRepository;
import com.example.psicowise_backend_spring.repository.consulta.PacienteRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;
    private final PacienteRepository pacienteRepository;
    private final PsicologoRepository psicologoRepository;
    private final UsuarioService usuarioService;

    /**
     * Cria um novo telefone
     *
     * @param dto DTO com os dados do telefone
     * @return O telefone criado
     */
    @Transactional
    public ResponseEntity<TelefoneDto> criarTelefone(CriarTelefoneDto dto) {
        try {
            Telefone telefone = new Telefone();
            telefone.setNumero(dto.numero());
            telefone.setDdd(dto.ddd());
            telefone.setCodigoPais(dto.codigoPais());
            telefone.setTipo(dto.tipo());
            telefone.setPrincipal(dto.principal());
            telefone.setWhatsapp(dto.whatsapp());
            telefone.setObservacao(dto.observacao());

            // Verificar se deve ser associado a um paciente
            if (dto.pacienteId() != null) {
                Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                        .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
                telefone.setPaciente(paciente);

                // Se for definido como principal, desativar outros telefones principais
                if (dto.principal()) {
                    atualizarTelefonePrincipalPaciente(paciente);
                }
            }

            // Verificar se deve ser associado a um psicólogo
            if (usuarioService.PegarUsuarioLogado() != null) {
                Psicologo psicologo = psicologoRepository
                        .findByUsuarioId(usuarioService.PegarUsuarioLogado().getBody().getId())
                        .orElseThrow(() -> new RuntimeException("Psicólogo não encontrado"));
                telefone.setPsicologo(psicologo);

                // Se for definido como principal, desativar outros telefones principais
                if (dto.principal()) {
                    atualizarTelefonePrincipalPsicologo(psicologo);
                }
            }

            // Salvar o telefone
            Telefone telefoneSalvo = telefoneRepository.save(telefone);
            return ResponseEntity.ok(converterParaDto(telefoneSalvo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Atualiza um telefone existente
     *
     * @param id ID do telefone
     * @param dto DTO com os novos dados
     * @return O telefone atualizado
     */
    @Transactional
    public ResponseEntity<TelefoneDto> atualizarTelefone(UUID id, CriarTelefoneDto dto) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            telefone.setNumero(dto.numero());
            telefone.setDdd(dto.ddd());
            telefone.setCodigoPais(dto.codigoPais());
            telefone.setTipo(dto.tipo());
            telefone.setWhatsapp(dto.whatsapp());
            telefone.setObservacao(dto.observacao());

            // Atualizar o status de principal apenas se tiver mudado
            if (dto.principal() != telefone.isPrincipal()) {
                telefone.setPrincipal(dto.principal());

                // Se for definido como principal, desativar outros telefones principais
                if (dto.principal()) {
                    if (telefone.getPaciente() != null) {
                        atualizarTelefonePrincipalPaciente(telefone.getPaciente());
                    } else if (telefone.getPsicologo() != null) {
                        atualizarTelefonePrincipalPsicologo(telefone.getPsicologo());
                    }
                }
            }

            Telefone telefoneSalvo = telefoneRepository.save(telefone);
            return ResponseEntity.ok(converterParaDto(telefoneSalvo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Exclui um telefone
     *
     * @param id ID do telefone
     * @return Mensagem de sucesso
     */
    @Transactional
    public ResponseEntity<String> excluirTelefone(UUID id) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            telefoneRepository.delete(telefone);
            return ResponseEntity.ok("Telefone excluído com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir telefone: " + e.getMessage());
        }
    }

    /**
     * Busca um telefone pelo ID
     *
     * @param id ID do telefone
     * @return O telefone encontrado
     */
    public ResponseEntity<TelefoneDto> buscarTelefonePorId(UUID id) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            return ResponseEntity.ok(converterParaDto(telefone));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Lista todos os telefones de um paciente
     *
     * @param pacienteId ID do paciente
     * @return Lista de telefones
     */
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPaciente(UUID pacienteId) {
        try {
            List<Telefone> telefones = telefoneRepository.findByPacienteId(pacienteId);

            List<TelefoneDto> telefonesDto = telefones.stream()
                    .map(this::converterParaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(telefonesDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Lista todos os telefones de um psicólogo
     *
     * @return Lista de telefones
     */
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorPsicologo() {
        try {
            UUID usuarioId = usuarioService.PegarUsuarioLogado().getBody().getId();
            List<Telefone> telefones = telefoneRepository.findByPsicologoId(usuarioId);

            List<TelefoneDto> telefonesDto = telefones.stream()
                    .map(this::converterParaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(telefonesDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtém o telefone WhatsApp principal de um paciente
     *
     * @param pacienteId ID do paciente
     * @return O número de telefone formatado para WhatsApp ou null
     */
    public String obterTelefoneWhatsappPaciente(UUID pacienteId) {
        List<Telefone> telefones = telefoneRepository.findWhatsappByPacienteId(pacienteId);
        if (telefones.isEmpty()) {
            return null;
        }
        return telefones.get(0).getNumeroFormatadoWhatsapp();
    }

    /**
     * Obtém o telefone WhatsApp principal de um psicólogo
     *
     * @param psicologoId ID do psicólogo
     * @return O número de telefone formatado para WhatsApp ou null
     */
    public String obterTelefoneWhatsappPsicologo(UUID psicologoId) {
        List<Telefone> telefones = telefoneRepository.findWhatsappByPsicologoId(psicologoId);
        if (telefones.isEmpty()) {
            return null;
        }
        return telefones.get(0).getNumeroFormatadoWhatsapp();
    }

    /**
     * Atualiza o status de principal dos telefones de um paciente
     *
     * @param paciente O paciente
     */
    private void atualizarTelefonePrincipalPaciente(Paciente paciente) {
        List<Telefone> telefonesPaciente = telefoneRepository.findByPaciente(paciente);
        telefonesPaciente.forEach(tel -> {
            tel.setPrincipal(false);
            telefoneRepository.save(tel);
        });
    }

    /**
     * Atualiza o status de principal dos telefones de um psicólogo
     *
     * @param psicologo O psicólogo
     */
    private void atualizarTelefonePrincipalPsicologo(Psicologo psicologo) {
        List<Telefone> telefonesPsicologo = telefoneRepository.findByPsicologo(psicologo);
        telefonesPsicologo.forEach(tel -> {
            tel.setPrincipal(false);
            telefoneRepository.save(tel);
        });
    }

    /**
     * Converte uma entidade Telefone para DTO
     *
     * @param telefone Entidade a ser convertida
     * @return DTO correspondente
     */
    private TelefoneDto converterParaDto(Telefone telefone) {
        return new TelefoneDto(
                telefone.getId(),
                telefone.getNumero(),
                telefone.getDdd(),
                telefone.getCodigoPais(),
                telefone.getTipo(),
                telefone.isPrincipal(),
                telefone.isWhatsapp(),
                telefone.getObservacao()
        );
    }
}