package com.example.psicowise_backend_spring.integration.consultas;


import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.enums.consulta.StatusConsulta;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.PacienteRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class SalaVideoRepositoryTest {

    @Autowired
    private SalaVideoRepository salaVideoRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PsicologoRepository psicologoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Consulta consulta;
    private SalaVideo salaVideo;

    @BeforeEach
    void setUp() {
        // Criar role
        Role rolePsicologo = new Role();
        rolePsicologo.setRole(ERole.PSICOLOGO);
        roleRepository.save(rolePsicologo);

        // Criar usuário para psicólogo
        Usuario usuarioPsicologo = new Usuario();
        usuarioPsicologo.setNome("Dr.");
        usuarioPsicologo.setSobrenome("Teste");
        usuarioPsicologo.setEmail("dr.teste@example.com");
        usuarioPsicologo.setSenha("senha123");
        usuarioRepository.save(usuarioPsicologo);

        // Criar psicólogo
        Psicologo psicologo = new Psicologo();
        psicologo.setUsuario(usuarioPsicologo);
        psicologo.setCrp("12345");
        psicologoRepository.save(psicologo);

        // Criar paciente
        Paciente paciente = new Paciente();
        paciente.setNome("Paciente");
        paciente.setSobrenome("Teste");
        paciente.setEmail("paciente.teste@example.com");
        paciente.setPsicologo(psicologo);
        pacienteRepository.save(paciente);

        // Criar consulta
        consulta = new Consulta();
        consulta.setPsicologo(psicologo);
        consulta.setPaciente(paciente);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setDuracaoMinutos(60);
        consulta.setStatus(StatusConsulta.AGENDADA);
        consultaRepository.save(consulta);

        // Criar sala de vídeo
        salaVideo = new SalaVideo();
        salaVideo.setConsulta(consulta);
        salaVideo.setSalaId("psicowise_test_123");
        salaVideo.setLinkAcesso("https://meet.jit.si/psicowise_test_123");
        salaVideo.setLinkHost("https://meet.jit.si/psicowise_test_123#config...");
        salaVideo.setAtiva(false);
        salaVideoRepository.save(salaVideo);
    }

    @Test
    @DisplayName("Deve encontrar sala por consulta")
    void testFindByConsulta() {
        // Act
        Optional<SalaVideo> resultado = salaVideoRepository.findByConsulta(consulta);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(salaVideo.getId(), resultado.get().getId());
        assertEquals("psicowise_test_123", resultado.get().getSalaId());
    }

    @Test
    @DisplayName("Deve encontrar sala por consultaId")
    void testFindByConsultaId() {
        // Act
        Optional<SalaVideo> resultado = salaVideoRepository.findByConsultaId(consulta.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(salaVideo.getId(), resultado.get().getId());
        assertEquals("psicowise_test_123", resultado.get().getSalaId());
    }

    @Test
    @DisplayName("Deve encontrar sala por salaId")
    void testFindBySalaId() {
        // Act
        Optional<SalaVideo> resultado = salaVideoRepository.findBySalaId("psicowise_test_123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(salaVideo.getId(), resultado.get().getId());
        assertEquals(consulta.getId(), resultado.get().getConsulta().getId());
    }

    @Test
    @DisplayName("Deve encontrar salas por status (ativa)")
    void testFindByAtiva() {
        // Arrange
        // Criar uma segunda sala ativa
        Consulta consulta2 = new Consulta();
        consulta2.setPsicologo(consulta.getPsicologo());
        consulta2.setPaciente(consulta.getPaciente());
        consulta2.setDataHora(LocalDateTime.now().plusDays(2));
        consulta2.setDuracaoMinutos(45);
        consulta2.setStatus(StatusConsulta.AGENDADA);
        consultaRepository.save(consulta2);

        SalaVideo salaAtiva = new SalaVideo();
        salaAtiva.setConsulta(consulta2);
        salaAtiva.setSalaId("psicowise_test_456");
        salaAtiva.setLinkAcesso("https://meet.jit.si/psicowise_test_456");
        salaAtiva.setLinkHost("https://meet.jit.si/psicowise_test_456#config...");
        salaAtiva.setAtiva(true);
        salaAtiva.setDataAtivacao(LocalDateTime.now());
        salaVideoRepository.save(salaAtiva);

        // Act
        List<SalaVideo> salasInativas = salaVideoRepository.findByAtiva(false);
        List<SalaVideo> salasAtivas = salaVideoRepository.findByAtiva(true);

        // Assert
        assertEquals(1, salasInativas.size());
        assertEquals(1, salasAtivas.size());
        assertEquals(salaVideo.getId(), salasInativas.get(0).getId());
        assertEquals(salaAtiva.getId(), salasAtivas.get(0).getId());
    }

    @Test
    @DisplayName("Deve encontrar salas por período de ativação")
    void testFindByDataAtivacaoBetween() {
        // Arrange
        // Ativar a sala existente
        salaVideo.setAtiva(true);
        salaVideo.setDataAtivacao(LocalDateTime.now().minusHours(2));
        salaVideoRepository.save(salaVideo);

        // Criar uma segunda sala com data de ativação diferente
        Consulta consulta2 = new Consulta();
        consulta2.setPsicologo(consulta.getPsicologo());
        consulta2.setPaciente(consulta.getPaciente());
        consulta2.setDataHora(LocalDateTime.now().plusDays(3));
        consulta2.setDuracaoMinutos(30);
        consulta2.setStatus(StatusConsulta.AGENDADA);
        consultaRepository.save(consulta2);

        SalaVideo salaNova = new SalaVideo();
        salaNova.setConsulta(consulta2);
        salaNova.setSalaId("psicowise_test_789");
        salaNova.setLinkAcesso("https://meet.jit.si/psicowise_test_789");
        salaNova.setLinkHost("https://meet.jit.si/psicowise_test_789#config...");
        salaNova.setAtiva(true);
        salaNova.setDataAtivacao(LocalDateTime.now().plusHours(1));
        salaVideoRepository.save(salaNova);

        // Act
        LocalDateTime inicio = LocalDateTime.now().minusHours(3);
        LocalDateTime fim = LocalDateTime.now().minusHours(1);
        List<SalaVideo> salasAntigas = salaVideoRepository.findByDataAtivacaoBetween(inicio, fim);

        LocalDateTime inicioRecente = LocalDateTime.now().minusHours(1);
        LocalDateTime fimRecente = LocalDateTime.now().plusHours(2);
        List<SalaVideo> salasRecentes = salaVideoRepository.findByDataAtivacaoBetween(inicioRecente, fimRecente);

        // Assert
        assertEquals(1, salasAntigas.size());
        assertEquals(1, salasRecentes.size());
        assertEquals(salaVideo.getId(), salasAntigas.get(0).getId());
        assertEquals(salaNova.getId(), salasRecentes.get(0).getId());
    }
}