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
    
    public Paciente criar(PacienteRequestDTO pacienteDTO) {
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
    
    public Paciente atualizar(UUID id, PacienteRequestDTO pacienteDTO) {
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
        // Implementação dependerá do serviço de telefone no sistema
        // A lógica seria algo como:
        
        // Criar um novo telefone
        Telefone telefone = new Telefone();
        telefone.setProprietarioId(paciente.getId());
        telefone.setTipoProprietario(TipoProprietario.PACIENTE);
        telefone.setPrincipal(true);
        telefone.setWhatsapp(true);
        
        // Formatar e salvar dados do telefone
        String numeroLimpo = numeroTelefone.replaceAll("[^0-9]", "");
        
        // Configurar DDD e número
        if (numeroLimpo.length() >= 11) { // Assumindo formato brasileiro
            telefone.setCodigoPais("+55");
            telefone.setDdd(numeroLimpo.substring(0, 2));
            telefone.setNumero(numeroLimpo.substring(2));
        } else {
            // Tratamento de formato inválido
            telefone.setDdd("00");
            telefone.setNumero(numeroLimpo);
            telefone.setCodigoPais("+55");
        }
        
        // Aqui seria necessário salvar o telefone usando o repositório apropriado
        // telefoneRepository.save(telefone);
    }
    
    private void atualizarTelefonePaciente(Paciente paciente, String numeroTelefone) {
        // Implementação similar à adição, mas verificando se já existe um telefone principal
        // para o paciente e atualizando-o em vez de criar um novo
    }
}
}