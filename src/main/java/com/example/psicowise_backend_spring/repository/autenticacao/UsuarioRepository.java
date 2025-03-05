package com.example.psicowise_backend_spring.repository.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}
