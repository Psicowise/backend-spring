package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PsicologoRepository extends JpaRepository<Psicologo, UUID> {
    Optional<Psicologo> findByCrp(String crp);
    List<Psicologo> findByEspecialidade(String especialidade);
}