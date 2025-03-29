package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
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

    // Adicione outros métodos conforme necessário
}