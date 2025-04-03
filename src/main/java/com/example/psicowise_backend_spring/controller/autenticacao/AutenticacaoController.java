package com.example.psicowise_backend_spring.controller.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.*;
import com.example.psicowise_backend_spring.exception.autenticacao.TokenInvalidoException;
import com.example.psicowise_backend_spring.service.autenticacao.AutenticacaoService;
import com.example.psicowise_backend_spring.service.autenticacao.RecuperacaoSenhaService;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final RecuperacaoSenhaService recuperacaoSenhaService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        return autenticacaoService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LogoutRequestDto request) {
        return autenticacaoService.logout(request.token());
    }

    @PostMapping("/esqueci")
    public ResponseEntity<String> esqueceuSenha(@RequestBody EsqueciSenhaRequestDto request) {
        try {
            recuperacaoSenhaService.enviarEmailRecuperacaoSenha(request.email());
            return ResponseEntity.ok("Email de recuperação enviado com sucesso");
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body("Erro ao enviar email de recuperação: " + e.getMessage());
        }
    }

    @PostMapping("/redefinir")
    public ResponseEntity<String> redefinirSenha(@RequestBody RedefinirSenhaRequestDto request) {
        try {
            recuperacaoSenhaService.redefinirSenha(request.token(), request.novaSenha());
            return ResponseEntity.ok("Senha redefinida com sucesso");
        } catch (TokenInvalidoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validar-token")
    public ResponseEntity<Boolean> validarToken(@RequestBody ValidarTokenRequestDto request) {
        boolean valido = recuperacaoSenhaService.validarToken(request.token());
        return ResponseEntity.ok(valido);
    }
}
