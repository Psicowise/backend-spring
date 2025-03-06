package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.HashUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private UsuarioRepository usuarioRepository;
    private RoleRepository roleRepository;
    private HashUtil hashUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, RoleRepository roleRepository, HashUtil hashUtil) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.hashUtil = hashUtil;
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
