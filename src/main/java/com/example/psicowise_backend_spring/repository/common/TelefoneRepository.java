package com.example.psicowise_backend_spring.repository.common;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.common.TipoTelefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, UUID> {

    List<Telefone> findByPaciente(Paciente paciente);

    List<Telefone> findByPacienteId(UUID pacienteId);

    List<Telefone> findByPsicologo(Psicologo psicologo);

    List<Telefone> findByPsicologoId(UUID psicologoId);

    List<Telefone> findByPacienteAndTipo(Paciente paciente, TipoTelefone tipo);

    List<Telefone> findByPsicologoAndTipo(Psicologo psicologo, TipoTelefone tipo);

    Optional<Telefone> findByPacienteAndPrincipal(Paciente paciente, boolean principal);

    Optional<Telefone> findByPsicologoAndPrincipal(Psicologo psicologo, boolean principal);

    List<Telefone> findByWhatsapp(boolean whatsapp);

    @Query("SELECT t FROM Telefone t WHERE t.paciente.id = :pacienteId AND t.whatsapp = true ORDER BY t.principal DESC")
    List<Telefone> findWhatsappByPacienteId(@Param("pacienteId") UUID pacienteId);

    @Query("SELECT t FROM Telefone t WHERE t.psicologo.id = :psicologoId AND t.whatsapp = true ORDER BY t.principal DESC")
    List<Telefone> findWhatsappByPsicologoId(@Param("psicologoId") UUID psicologoId);
}
