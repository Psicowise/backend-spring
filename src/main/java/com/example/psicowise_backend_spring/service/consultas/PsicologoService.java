package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.dto.autenticacao.UsuarioLogadoDto;
import com.example.psicowise_backend_spring.dto.consultas.CriarPsicologoDto;
import com.example.psicowise_backend_spring.dto.consultas.PsicologoDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.entity.consulta.Especialidade;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.repository.consulta.EspecialidadeRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import com.example.psicowise_backend_spring.util.HashUtil;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PsicologoService {

    private final PsicologoRepository psicologoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final HashUtil hashUtil;
    private final UsuarioService usuarioService;
    private final EspecialidadeRepository especialidadeRepository;


    PsicologoService(
            PsicologoRepository psicologoRepository,
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            HashUtil hashUtil,
            UsuarioService usuarioService,
            EspecialidadeRepository especialidadeRepository) {
        this.psicologoRepository = psicologoRepository;
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.hashUtil = hashUtil;
        this.usuarioService = usuarioService;
        this.especialidadeRepository = especialidadeRepository;
    }

    @Transactional
    public ResponseEntity<Psicologo> criarPsicologo(CriarPsicologoDto criarPsicologoDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idString = auth.getName();
        UUID usuarioId = UUID.fromString(idString);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        // Verificar se já existe um psicólogo para este usuário
        if (psicologoRepository.findByUsuarioId(usuarioId).isPresent()) {
            throw new UsuarioException("Este usuário já está registrado como psicólogo");
        }

        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuario);
        psicologo.setCrp(criarPsicologoDto.crp());

        // Processar especialidades
        List<Especialidade> especialidades = new ArrayList<>();
        for (String nomeEspecialidade : criarPsicologoDto.especialidade()) {
            Especialidade especialidade = especialidadeRepository.findByNomeEspecialidade(nomeEspecialidade)
                    .orElseGet(() -> {
                        // Se não encontrar a especialidade, cria uma nova
                        Especialidade novaEspecialidade = new Especialidade();
                        novaEspecialidade.setNomeEspecialidade(nomeEspecialidade);
                        return especialidadeRepository.save(novaEspecialidade);
                    });
            especialidades.add(especialidade);
        }

        psicologo.setEspecialidades(especialidades);

        Psicologo saved = psicologoRepository.save(psicologo);

        return ResponseEntity.ok(saved);
    }
}
