package com.example.psicowise_backend_spring.controller.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.UsuarioLogadoDto;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UsuarioAtualController {

    final private UsuarioService usuarioService;

    public UsuarioAtualController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //@GetMapping("/atual")
    //public ResponseEntity<UsuarioLogadoDto> obterUsuarioAtual() {
    //    return usuarioService.PegarUsuarioLogado();
    //}

    @GetMapping("/atual")
    public ResponseEntity<?> test() {
        return ResponseEntity.notFound().build();
    }

}