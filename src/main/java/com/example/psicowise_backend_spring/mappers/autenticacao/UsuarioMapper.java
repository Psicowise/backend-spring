package com.example.psicowise_backend_spring.mappers.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.UsuarioLogadoDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioLogadoDto converterParaDTO(Usuario usuario) {
        UsuarioLogadoDto dto = new UsuarioLogadoDto();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNome(usuario.getNome());
        dto.setSobrenome(usuario.getSobrenome());
        dto.setCreatedAt(usuario.getCreatedAt());
        dto.setUpdatedAt(usuario.getUpdatedAt());
        return dto;
    }
}
