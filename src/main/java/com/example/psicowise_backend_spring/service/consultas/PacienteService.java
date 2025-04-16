package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.dto.consultas.PacienteRequestDto;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.repository.common.TelefoneRepository;
import com.example.psicowise_backend_spring.repository.consulta.PacienteRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import com.example.psicowise_backend_spring.service.common.TelefoneLoaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final TelefoneLoaderService telefoneLoaderService;
    private final UsuarioService usuarioService;
    private final PsicologoRepository psicologoRepository;
    private final TelefoneRepository telefoneRepository;

    public Optional<Paciente> buscarPorId(UUID id) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        pacienteOpt.ifPresent(telefoneLoaderService::carregarTelefonesPaciente);
        return pacienteOpt;
    }

    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        telefoneLoaderService.carregarTelefonesPacientes(pacientes);
        return pacientes;
    }

    public List<Paciente> buscarPorPsicologoLogado() {
        UUID usuarioId = usuarioService.PegarUsuarioLogado().getBody().getId();

        return psicologoRepository.findByUsuarioId(usuarioId)
                .map(psicologo -> {
                    List<Paciente> pacientes = pacienteRepository.findByPsicologoId(psicologo.getId());
                    telefoneLoaderService.carregarTelefonesPacientes(pacientes);
                    return pacientes;
                })
                .orElse(List.of());
    }

    public Paciente criar(PacienteRequestDto pacienteDTO) {
        // Obter o psicólogo logado
        UUID usuarioId = usuarioService.PegarUsuarioLogado().getBody().getId();
        Psicologo psicologo = psicologoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Psicólogo não encontrado"));

        // Criar e salvar paciente
        Paciente paciente = new Paciente();
        paciente.setNome(pacienteDTO.getNome());
        paciente.setSobrenome(pacienteDTO.getSobrenome());
        paciente.setEmail(pacienteDTO.getEmail());
        paciente.setPsicologo(psicologo);

        // Salvar paciente
        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        // Adicionar telefone ao paciente se fornecido
        if (pacienteDTO.getTelefone() != null && !pacienteDTO.getTelefone().isEmpty()) {
            adicionarTelefonePaciente(pacienteSalvo, pacienteDTO.getTelefone());
        }

        return pacienteSalvo;
    }

    public Paciente atualizar(UUID id, PacienteRequestDto pacienteDTO) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setNome(pacienteDTO.getNome());
        paciente.setSobrenome(pacienteDTO.getSobrenome());
        paciente.setEmail(pacienteDTO.getEmail());

        // Atualizar telefone se fornecido
        if (pacienteDTO.getTelefone() != null && !pacienteDTO.getTelefone().isEmpty()) {
            atualizarTelefonePaciente(paciente, pacienteDTO.getTelefone());
        }

        return pacienteRepository.save(paciente);
    }

    public void excluir(UUID id) {
        pacienteRepository.deleteById(id);
    }

    private void adicionarTelefonePaciente(Paciente paciente, String numeroTelefone) {
        Telefone telefone = new Telefone();
        telefone.setProprietarioId(paciente.getId());
        telefone.setTipoProprietario(TipoProprietario.PACIENTE);
        telefone.setPrincipal(true); // Assuming the first phone added is the primary
        telefone.setWhatsapp(false); // Defaulting to not being WhatsApp

        String numeroLimpo = numeroTelefone.replaceAll("[^0-9]", "");

        if (numeroLimpo.length() >= 11) {
            telefone.setCodigoPais("+55");
            telefone.setDdd(numeroLimpo.substring(0, 2));
            telefone.setNumero(numeroLimpo.substring(2));
        } else {
            telefone.setDdd("00");
            telefone.setNumero(numeroLimpo);
            telefone.setCodigoPais("+55");
        }

        telefoneRepository.save(telefone);
    }

    private void atualizarTelefonePaciente(Paciente paciente, String numeroTelefone) {
        // Find existing primary phone for the patient
        Telefone existingPhone = telefoneRepository.findByProprietarioIdAndTipoProprietarioAndPrincipal(paciente.getId(), TipoProprietario.PACIENTE, true);

        if (existingPhone != null) {
            // Update existing phone
            String numeroLimpo = numeroTelefone.replaceAll("[^0-9]", "");
            if (numeroLimpo.length() >= 11) {
                existingPhone.setCodigoPais("+55");
                existingPhone.setDdd(numeroLimpo.substring(0, 2));
                existingPhone.setNumero(numeroLimpo.substring(2));
            } else {
                existingPhone.setDdd("00");
                existingPhone.setNumero(numeroLimpo);
                existingPhone.setCodigoPais("+55");
            }
            telefoneRepository.save(existingPhone);
        } else {
            // If no primary phone exists, add a new one
            adicionarTelefonePaciente(paciente, numeroTelefone);
        }
    }
}