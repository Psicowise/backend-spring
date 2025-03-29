package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.consultas.SalaVideoDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import com.example.psicowise_backend_spring.service.consultas.SalaVideoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalaVideoServiceTest {

    @Mock
    private SalaVideoRepository salaVideoRepository;

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private PsicologoRepository psicologoRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private SalaVideoService salaVideoService;

    private UUID consultaId;
    private UUID salaVideoId;
    private UUID psicologoUsuarioId;
    private UUID pacienteId;
    private Consulta consulta;
    private Psicologo psicologo;
    private Usuario usuario;
    private Paciente paciente;
    private SalaVideo salaVideo;

    @BeforeEach
    void setUp() {
        // Configurar IDs
        consultaId = UUID.randomUUID();
        salaVideoId = UUID.randomUUID();
        psicologoUsuarioId = UUID.randomUUID();
        pacienteId = UUID.randomUUID();

        // Configurar objetos
        usuario = new Usuario();
        usuario.setId(psicologoUsuarioId);

        psicologo = new Psicologo();
        psicologo.setId(UUID.randomUUID());
        psicologo.setUsuario(usuario);

        paciente = new Paciente();
        paciente.setId(pacienteId);

        consulta = new Consulta();
        consulta.setId(consultaId);
        consulta.setPsicologo(psicologo);
        consulta.setPaciente(paciente);

        salaVideo = new SalaVideo();
        salaVideo.setId(salaVideoId);
        salaVideo.setConsulta(consulta);
        salaVideo.setSalaId("psicowise_test_123");
        salaVideo.setLinkAcesso("https://meet.jit.si/psicowise_test_123");
        salaVideo.setLinkHost("https://meet.jit.si/psicowise_test_123#config...");
        salaVideo.setAtiva(false);

        // Configurar MockSecurityContext
        when(authentication.getName()).thenReturn(psicologoUsuarioId.toString());
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Configurar valor para propriedade do webrtcServer
        ReflectionTestUtils.setField(salaVideoService, "webrtcServer", "https://meet.jit.si");
    }

    @Test
    @DisplayName("Deve criar uma sala de vídeo com sucesso")
    void testCriarSalaVideoSucesso() {
        // Arrange
        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));
        when(salaVideoRepository.findByConsulta(consulta)).thenReturn(Optional.empty());
        when(salaVideoRepository.save(any(SalaVideo.class))).thenAnswer(invocation -> {
            SalaVideo sala = invocation.getArgument(0);
            sala.setId(salaVideoId);
            return sala;
        });

        // Act
        ResponseEntity<SalaVideoDto> response = salaVideoService.criarSalaVideo(consultaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(consultaId, response.getBody().consultaId());
        assertTrue(response.getBody().linkAcesso().startsWith("https://meet.jit.si/"));
        assertFalse(response.getBody().ativa());

        verify(consultaRepository, times(1)).findById(consultaId);
        verify(psicologoRepository, times(1)).findByUsuarioId(psicologoUsuarioId);
        verify(salaVideoRepository, times(1)).findByConsulta(consulta);
        verify(salaVideoRepository, times(1)).save(any(SalaVideo.class));
    }

    @Test
    @DisplayName("Deve retornar a sala existente se já existir")
    void testCriarSalaVideoJaExistente() {
        // Arrange
        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));
        when(salaVideoRepository.findByConsulta(consulta)).thenReturn(Optional.of(salaVideo));

        // Act
        ResponseEntity<SalaVideoDto> response = salaVideoService.criarSalaVideo(consultaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(salaVideoId, response.getBody().id());
        assertEquals("psicowise_test_123", response.getBody().salaId());

        verify(salaVideoRepository, never()).save(any(SalaVideo.class));
    }

    @Test
    @DisplayName("Deve negar acesso quando o psicólogo não é o responsável pela consulta")
    void testCriarSalaVideoPsicologoNaoAutorizado() {
        // Arrange
        UUID outroPsicologoId = UUID.randomUUID();
        Psicologo outroPsicologo = new Psicologo();
        outroPsicologo.setId(outroPsicologoId);

        consulta.setPsicologo(outroPsicologo);

        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));

        // Act
        ResponseEntity<SalaVideoDto> response = salaVideoService.criarSalaVideo(consultaId);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        verify(salaVideoRepository, never()).save(any(SalaVideo.class));
    }

    @Test
    @DisplayName("Deve ativar uma sala com sucesso")
    void testAtivarSalaSucesso() {
        // Arrange
        when(salaVideoRepository.findById(salaVideoId)).thenReturn(Optional.of(salaVideo));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));
        when(salaVideoRepository.save(any(SalaVideo.class))).thenReturn(salaVideo);

        // Act
        ResponseEntity<String> response = salaVideoService.ativarSala(salaVideoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sala ativada com sucesso", response.getBody());

        verify(salaVideoRepository, times(1)).findById(salaVideoId);
        verify(salaVideoRepository, times(1)).save(any(SalaVideo.class));
    }

    @Test
    @DisplayName("Deve obter informações da sala com sucesso para o psicólogo")
    void testObterSalaVideoPsicologo() {
        // Arrange
        when(consultaRepository.findById(consultaId)).thenReturn(Optional.of(consulta));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));
        when(salaVideoRepository.findByConsulta(consulta)).thenReturn(Optional.of(salaVideo));

        // Act
        ResponseEntity<SalaVideoDto> response = salaVideoService.obterSalaVideo(consultaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(salaVideoId, response.getBody().id());
        assertEquals("psicowise_test_123", response.getBody().salaId());
    }

    @Test
    @DisplayName("Deve desativar uma sala com sucesso")
    void testDesativarSalaSucesso() {
        // Arrange
        salaVideo.setAtiva(true);
        salaVideo.setDataAtivacao(LocalDateTime.now().minusHours(1));

        when(salaVideoRepository.findById(salaVideoId)).thenReturn(Optional.of(salaVideo));
        when(psicologoRepository.findByUsuarioId(psicologoUsuarioId)).thenReturn(Optional.of(psicologo));
        when(salaVideoRepository.save(any(SalaVideo.class))).thenReturn(salaVideo);

        // Act
        ResponseEntity<String> response = salaVideoService.desativarSala(salaVideoId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sala desativada com sucesso", response.getBody());

        verify(salaVideoRepository, times(1)).findById(salaVideoId);
        verify(salaVideoRepository, times(1)).save(any(SalaVideo.class));
    }
}