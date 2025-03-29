package com.example.psicowise_backend_spring.service.common;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.repository.common.TelefoneRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Serviço para carregar telefones relacionados a pacientes e psicólogos
 */
@Service
@RequiredArgsConstructor
public class TelefoneLoaderService {

    private final TelefoneRepository telefoneRepository;

    /**
     * Carrega os telefones de um paciente
     *
     * @param paciente Paciente para o qual carregar os telefones
     */
    public void carregarTelefonesPaciente(Paciente paciente) {
        if (paciente == null || paciente.getId() == null) {
            return;
        }

        List<Telefone> telefones = telefoneRepository.findByProprietarioIdAndTipoProprietario(
                paciente.getId(), TipoProprietario.PACIENTE);
        paciente.setTelefones(telefones);
    }

    /**
     * Carrega os telefones de um psicólogo
     *
     * @param psicologo Psicólogo para o qual carregar os telefones
     */
    public void carregarTelefonesPsicologo(Psicologo psicologo) {
        if (psicologo == null || psicologo.getId() == null) {
            return;
        }

        List<Telefone> telefones = telefoneRepository.findByProprietarioIdAndTipoProprietario(
                psicologo.getId(), TipoProprietario.PSICOLOGO);
        psicologo.setTelefones(telefones);
    }

    /**
     * Carrega os telefones de uma lista de pacientes
     *
     * @param pacientes Lista de pacientes
     */
    public void carregarTelefonesPacientes(List<Paciente> pacientes) {
        if (pacientes == null || pacientes.isEmpty()) {
            return;
        }

        pacientes.forEach(this::carregarTelefonesPaciente);
    }

    /**
     * Carrega os telefones de uma lista de psicólogos
     *
     * @param psicologos Lista de psicólogos
     */
    public void carregarTelefonesPsicologos(List<Psicologo> psicologos) {
        if (psicologos == null || psicologos.isEmpty()) {
            return;
        }

        psicologos.forEach(this::carregarTelefonesPsicologo);
    }
}