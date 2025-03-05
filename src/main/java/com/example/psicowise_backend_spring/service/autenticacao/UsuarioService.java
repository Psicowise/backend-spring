package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
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

    public ResponseEntity<Usuario> CriarUsuario(CriarUsuarioDto usuarioDto){

        var usuario = new Usuario();

        usuario.setNome(usuarioDto.nome());
        usuario.setSobrenome(usuarioDto.sobrenome());

        // Validar email se o email já está cadastrado

        usuarioRepository.findByEmail(usuarioDto.email()).ifPresent(u -> {
            throw new RuntimeException("Email já cadastrado");
        });

        usuario.setEmail(usuarioDto.email());

        // Hashizar senha
        usuario.setSenha(hashUtil.hashPassword(usuarioDto.senha()));

        // Verificar se o role informado existe, caso contrário atribuir o role padrão "usuario"
        var roleOpt = roleRepository.findByRole(usuarioDto.role());
        if (roleOpt.isPresent()) {
            usuario.setRole(roleOpt.get());
        } else {
            var usuarioRole = roleRepository.findByRole("usuario")
                    .orElseThrow(() -> new RuntimeException("Role padrão 'usuario' não encontrada"));
            usuario.setRole(usuarioRole);
        }

        usuarioRepository.save(usuario);

        return ResponseEntity.ok(usuario);
    }

    public ResponseEntity<List<Usuario>> ListarUsuarios(){
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    public ResponseEntity<Usuario> BuscarUsuarioPorEmail(String email){
        return ResponseEntity.ok(usuarioRepository.findByEmail(email).get());
    }

    public ResponseEntity<Usuario> BuscarUsuarioPorId(UUID id){
        return ResponseEntity.ok(usuarioRepository.findById(id).get());
    }

    public ResponseEntity<Usuario> AtualizarUsuario(Usuario usuario){
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }
}
