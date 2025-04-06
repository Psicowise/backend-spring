package com.example.psicowise_backend_spring.controller.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.dto.autenticacao.UsuarioLogadoDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Endpoint para criar um novo usuário
    @PostMapping("/criar/comum")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody CriarUsuarioDto usuarioDto) {
        return usuarioService.CriarUsuarioComum(usuarioDto);
    }

    @PostMapping("/criar/psicologo")
    public ResponseEntity<Usuario> criarUsuarioPsicologo(@RequestBody CriarUsuarioDto usuarioDto) {
        return usuarioService.CriarUsuarioPsicologo(usuarioDto);
    }

    @GetMapping(  "/usuariologado")
    public ResponseEntity<UsuarioLogadoDto> buscarUsuarioLogado() {
        return usuarioService.PegarUsuarioLogado();
    }

    // Endpoint para listar todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return usuarioService.ListarUsuarios();
    }

    // Endpoint para buscar um usuário pelo email
    @GetMapping("/email")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        return usuarioService.BuscarUsuarioPorEmail(email);
    }

    // Endpoint para buscar um usuário pelo id
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable UUID id) {
        return usuarioService.BuscarUsuarioPorId(id);
    }

    // Endpoint para atualizar um usuário
    @PutMapping
    public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario) {
        return usuarioService.AtualizarUsuario(usuario);
    }
}
