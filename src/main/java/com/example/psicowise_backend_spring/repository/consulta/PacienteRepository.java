package com.example.psicowise_backend_spring.repository.consulta;

import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {

    Optional<Paciente> findByEmail(String email);
    List<Paciente> findByPsicologo(Psicologo psicologo);
    List<Paciente> findByPsicologoId(UUID psicologoId);
}
