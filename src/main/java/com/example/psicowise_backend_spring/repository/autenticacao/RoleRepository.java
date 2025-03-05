package com.example.psicowise_backend_spring.repository.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRole(String role);
}
