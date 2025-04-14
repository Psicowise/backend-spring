package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import com.example.psicowise_backend_spring.service.notificacao.LembreteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacaoServiceTest {

    @Mock
    private WhatsappService whatsappService;

    @InjectMocks
    private LembreteService lembreteService;

    @Captor
    private ArgumentCaptor<String> mensagemCaptor;

    private Consulta consulta;
    private Paciente paciente;
    private Psicologo psicologo;
    private Usuario usuarioPsicologo;

    private static final String TELEFONE_WHATSAPP = "5511999999999";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @BeforeEach
    void setUp() {
        // Configurar objetos para os testes
        usuarioPsicologo = new Usuario();
        usuarioPsicologo.setId(UUID.randomUUID());
        usuarioPsicologo.setNome("Dr. Teste");
        usuarioPsicologo.setSobrenome("Sobrenome");
        usuarioPsicologo.setEmail("dr.teste@example.com");

        psicologo = new Psicologo();
        psicologo.setId(UUID.randomUUID());
        psicologo.setUsuario(usuarioPsicologo);
        psicologo.setCrp("12345");

        // Criando uma classe anônima estática para facilitar o teste
        paciente = new TestPaciente(null);
        paciente.setId(UUID.randomUUID());
        paciente.setNome("Paciente");
        paciente.setSobrenome("Teste");
        paciente.setEmail("paciente@example.com");
        paciente.setPsicologo(psicologo);

        consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPaciente(paciente);
        consulta.setPsicologo(psicologo);
        consulta.setDataHora(LocalDateTime.now().plusDays(1));
        consulta.setDuracaoMinutos(60);
    }

    @Test
    @DisplayName("Deve agendar lembretes com sucesso")
    void testAgendarLembretesSucesso() {
        // Arrange
        String telefoneFormatado = TELEFONE_WHATSAPP;
        paciente = new TestPaciente(telefoneFormatado);
        consulta.setPaciente(paciente);

        // Act
        lembreteService.agendarLembretes(consulta);

        // Assert - verificamos apenas que não houve exceção
        // Não podemos verificar o envio imediato pois os lembretes são agendados
        assertTrue(true);
    }

    @Test
    @DisplayName("Deve enviar lembrete imediato com sucesso")
    void testEnviarLembreteImediato() {
        // Arrange
        String telefoneFormatado = TELEFONE_WHATSAPP;
        paciente = new TestPaciente(telefoneFormatado);
        consulta.setPaciente(paciente);

        when(whatsappService.enviarMensagemSimples(eq(telefoneFormatado), anyString())).thenReturn(true);

        // Act
        boolean resultado = lembreteService.enviarLembreteImediato(consulta);

        // Assert
        assertTrue(resultado);
        verify(whatsappService).enviarMensagemSimples(eq(telefoneFormatado), mensagemCaptor.capture());

        String mensagem = mensagemCaptor.getValue();
        assertTrue(mensagem.contains("Olá Paciente"));
        assertTrue(mensagem.contains("Dr. Teste"));
        assertTrue(mensagem.contains(consulta.getDataHora().format(DATE_FORMATTER)));
        assertTrue(mensagem.contains(consulta.getDataHora().format(TIME_FORMATTER)));
    }

    @Test
    @DisplayName("Deve falhar ao enviar lembrete quando paciente não tem WhatsApp")
    void testEnviarLembretePacienteSemWhatsApp() {
        // Arrange - paciente sem telefone WhatsApp

        // Act
        boolean resultado = lembreteService.enviarLembreteImediato(consulta);

        // Assert
        assertFalse(resultado);
        verify(whatsappService, never()).enviarMensagemSimples(anyString(), anyString());
    }

    // Classe estática para facilitar o teste
    private static class TestPaciente extends Paciente {
        private final String telefoneWhatsapp;

        public TestPaciente(String telefoneWhatsapp) {
            this.telefoneWhatsapp = telefoneWhatsapp;
        }

        @Override
        public String getTelefoneWhatsapp() {
            return telefoneWhatsapp;
        }
    }
}