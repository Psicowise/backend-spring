package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HashUtil hashUtil;

    public ResponseEntity<Usuario> CriarUsuario(CriarUsuarioDto usuarioDto) {
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
            var roleOpt = roleRepository.findByRole(usuarioDto.role());
            if (roleOpt.isPresent()) {
                usuario.setRole(roleOpt.get());
            } else {
                var usuarioRole = roleRepository.findByRole("usuario")
                        .orElseThrow(() -> new RoleNaoEncontradaException("usuario"));
                usuario.setRole(usuarioRole);
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
            return ResponseEntity.ok(usuarioRepository.findByEmail(email).get());
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
}
