package com.example.psicowise_backend_spring.dto.autenticacao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogadoDto {
    private UUID id;
    private String email;
    private String nome;
    private String sobrenome;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
