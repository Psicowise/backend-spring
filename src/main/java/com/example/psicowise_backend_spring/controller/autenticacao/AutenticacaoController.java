package com.example.psicowise_backend_spring.controller.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.LoginRequestDto;
import com.example.psicowise_backend_spring.dto.autenticacao.LoginResponseDto;
import com.example.psicowise_backend_spring.service.autenticacao.AutenticacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        return autenticacaoService.login(request);
    }
}
