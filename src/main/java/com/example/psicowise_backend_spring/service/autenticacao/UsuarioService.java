package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.dto.autenticacao.UsuarioLogadoDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.mappers.autenticacao.UsuarioMapper;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.HashUtil;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);


    private UsuarioRepository usuarioRepository;
    private RoleRepository roleRepository;
    private HashUtil hashUtil;
    private UsuarioMapper usuarioMapper;

    @Autowired
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            HashUtil hashUtil,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.hashUtil = hashUtil;
        this.usuarioMapper = usuarioMapper;
    }



    public ResponseEntity<Usuario> CriarUsuarioPsicologo (CriarUsuarioDto usuarioDto){
        try {

            var usuario = new Usuario();

            usuario.setNome(usuarioDto.nome());
            usuario.setSobrenome(usuarioDto.sobrenome());

            // Validar email se o email já está cadastrado
            if (usuarioRepository.findByEmail(usuarioDto.email()).isPresent()) {
                throw new EmailJaCadastradoException();
            }

            usuario.setEmail(usuarioDto.email());

            // Hashizar senha
            usuario.setSenha(hashUtil.hashPassword(usuarioDto.senha()));

            var roleOpt = roleRepository.findByRole(ERole.PSICOLOGO);
            if (roleOpt.isPresent()) {
                usuario.setRoles(List.of(roleOpt.get()));
            } else {
                var newRolePsicologo = new Role();
                newRolePsicologo.setRole(ERole.PSICOLOGO);
                var usuarioRolePsicologo = roleRepository.save(newRolePsicologo);
                usuario.setRoles(List.of(usuarioRolePsicologo));
            }

            usuarioRepository.save(usuario);

            return ResponseEntity.ok(usuario);
        } catch (EmailJaCadastradoException e) {
            throw e;
        } catch (RoleNaoEncontradaException e) {
            throw e;
        }
    }


    public ResponseEntity<Usuario> CriarUsuarioComum(CriarUsuarioDto usuarioDto) {
        try {
            var usuario = new Usuario();

            usuario.setNome(usuarioDto.nome());
            usuario.setSobrenome(usuarioDto.sobrenome());

            // Validar email se o email já está cadastrado
            if (usuarioRepository.findByEmail(usuarioDto.email()).isPresent()) {
                throw new EmailJaCadastradoException();
            }

            usuario.setEmail(usuarioDto.email());

            // Hashizar senha
            usuario.setSenha(hashUtil.hashPassword(usuarioDto.senha()));

            // Verificar se o role informado existe, caso contrário atribuir o role padrão "usuario"
            var roleOpt = roleRepository.findByRole(ERole.USER);
            if (roleOpt.isPresent()) {
                usuario.setRoles(List.of(roleOpt.get()));
            } else {
                var newRole = new Role();
                newRole.setRole(ERole.USER);
                var usuarioRole = roleRepository.save(newRole);
                usuario.setRoles(List.of(usuarioRole));
            }

            usuarioRepository.save(usuario);

            return ResponseEntity.ok(usuario);
        } catch (EmailJaCadastradoException | RoleNaoEncontradaException e) {
            // Estas exceções serão tratadas pelo GlobalExceptionHandler
            throw e;
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<List<Usuario>> ListarUsuarios() {
        try {
            return ResponseEntity.ok(usuarioRepository.findAll());
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Usuario> BuscarUsuarioPorEmail (String email){
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                // Retorna 404 quando o usuário não é encontrado
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Usuario> BuscarUsuarioPorId (UUID id){
        try{
            return ResponseEntity.ok(usuarioRepository.findById(id).get());
        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Usuario> AtualizarUsuario (Usuario usuario){
        try {
            // Verificar se o usuário existe
            if (!usuarioRepository.existsById(usuario.getId())) {
                throw new UsuarioNaoEncontradoException("id: " + usuario.getId());
            }

            // Verificar se está tentando atualizar para um email já existente
            usuarioRepository.findByEmail(usuario.getEmail())
                    .ifPresent(u -> {
                        if (!u.getId().equals(usuario.getId())) {
                            throw new EmailJaCadastradoException();
                        }
                    });

            return ResponseEntity.ok(usuarioRepository.save(usuario));
        } catch (UsuarioNaoEncontradoException | EmailJaCadastradoException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            throw e;
        }
    }

    private UsuarioLogadoDto buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ExpressionException("Usuário não encontrado com o email: " + email));

        return usuarioMapper.converterParaDTO(usuario);
    }

    public ResponseEntity<UsuarioLogadoDto> PegarUsuarioLogado (){
        logger.info("Recuperando usuário logado.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Obter o ID do usuário autenticado em formato de string
        String idString = auth.getName();
        logger.debug("Auth: {}", auth);
        logger.debug("Auth name: {}", auth.getName());
        logger.debug("Auth authorities: {}", auth.getAuthorities());

        // Converter a string para UUID
        UUID usuarioId;
        usuarioId = UUID.fromString(idString);

        if(usuarioId == null) {
          logger.error("Falha ao converter id '{}' para UUID: {}", idString);
        } else {
            logger.info("Id do Usuario retornado com sucesso '{}' para UUID: {}", idString);
        }


        // Buscar o usuário pelo ID
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new UsuarioNaoEncontradoException("id: " + usuarioId));

        UsuarioLogadoDto usuarioLogado = usuarioMapper.converterParaDTO(usuario);

        logger.info("Usuário logado recuperado com sucesso: {}", usuario.getEmail());
        return ResponseEntity.ok(usuarioLogado);
    }
}
