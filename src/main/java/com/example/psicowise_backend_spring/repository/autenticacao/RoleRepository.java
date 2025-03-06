package com.example.psicowise_backend_spring.repository.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByRole(ERole role);
}
