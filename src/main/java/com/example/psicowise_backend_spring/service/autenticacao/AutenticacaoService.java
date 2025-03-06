package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.LoginRequestDto;
import com.example.psicowise_backend_spring.dto.autenticacao.LoginResponseDto;
import com.example.psicowise_backend_spring.exception.autenticacao.CredenciaisInvalidasException;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.HashUtil;
import com.example.psicowise_backend_spring.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final HashUtil hashUtil;
    private final JwtUtil jwtUtil;


    public ResponseEntity<LoginResponseDto> login(LoginRequestDto request) {

        try {
            var usuario = usuarioRepository.findByEmail(request.email())
                    .orElseThrow(() -> new Exception("Usuário não encontrado"));

            if (usuario == null) {
                return ResponseEntity.status(401).body(null);
            }

            if (!hashUtil.verificarSenha(request.senha(), usuario.getSenha())) {
                throw new CredenciaisInvalidasException("Email ou senha inválidos");
            }

            var roles = usuario.getRoles().stream()
                    .map(role -> role.getRole().name())
                    .collect(Collectors.toList());

            String token = jwtUtil.generateToken(usuario);

            return ResponseEntity.ok(new LoginResponseDto(
                    token,
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getSobrenome(),
                    usuario.getEmail(),
                    roles
            ));

        } catch (Exception e) {
            // Log do erro
            System.err.println("Erro ao fazer login: " + e.getMessage());
            throw new CredenciaisInvalidasException("Falha na autenticação: " + e.getMessage());
        }
    }
}
