
package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import com.example.psicowise_backend_spring.service.notificacao.LembreteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import com.example.psicowise_backend_spring.entity.common.Telefone;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LembreteServiceTest {

    @Mock
    private WhatsappService whatsappService;

    @InjectMocks
    private LembreteService lembreteService;

    private Consulta consulta;
    private Paciente paciente;
    private Psicologo psicologo;
    private Usuario usuarioPsicologo;

    @BeforeEach
    void setUp() {
        // Configurar usuário do psicólogo
        usuarioPsicologo = new Usuario();
        usuarioPsicologo.setId(UUID.randomUUID());
        usuarioPsicologo.setNome("Dr. Teste");

        // Configurar psicólogo
        psicologo = new Psicologo();
        psicologo.setId(UUID.randomUUID());
        psicologo.setUsuario(usuarioPsicologo);

        // Configurar paciente
        paciente = new Paciente();
        paciente.setId(UUID.randomUUID());
        paciente.setNome("Paciente Teste");
        
        // Configurar telefones do paciente
        List<Telefone> telefones = new ArrayList<>();
        Telefone telefoneWhatsapp = new Telefone();
        telefoneWhatsapp.setNumero("+5511999999999");
        telefoneWhatsapp.setWhatsapp(true);
        telefoneWhatsapp.setPrincipal(true);
        telefones.add(telefoneWhatsapp);
        ReflectionTestUtils.setField(paciente, "telefones", telefones);

        // Configurar consulta
        consulta = new Consulta();
        consulta.setId(UUID.randomUUID());
        consulta.setPsicologo(psicologo);
        consulta.setPaciente(paciente);
        consulta.setDataHora(LocalDateTime.now().plusDays(3));
    }

    @Test
    @DisplayName("Deve agendar lembretes com sucesso para uma consulta")
    void agendarLembretesComSucesso() {
        // Act & Assert - verificar se o método foi executado sem exceções
        // Não temos como verificar diretamente o agendamento pois é armazenado em um mapa privado
        assertDoesNotThrow(() -> lembreteService.agendarLembretes(consulta));
    }

    @Test
    @DisplayName("Não deve agendar lembretes quando a consulta é nula")
    void naoAgendarLembretesQuandoConsultaNula() {
        // Act & Assert
        assertDoesNotThrow(() -> lembreteService.agendarLembretes(null));
    }

    @Test
    @DisplayName("Não deve agendar lembretes quando a data da consulta é nula")
    void naoAgendarLembretesQuandoDataConsultaNula() {
        // Arrange
        consulta.setDataHora(null);
        
        // Act & Assert
        assertDoesNotThrow(() -> lembreteService.agendarLembretes(consulta));
    }

    @Test
    @DisplayName("Deve enviar lembrete imediato com sucesso")
    void enviarLembreteImediatoComSucesso() {
        // Arrange
        when(whatsappService.enviarMensagemSimples(anyString(), anyString())).thenReturn(true);
        
        // Act
        boolean resultado = lembreteService.enviarLembreteImediato(consulta);
        
        // Assert
        assertTrue(resultado);
        verify(whatsappService).enviarMensagemSimples(eq(paciente.getTelefoneWhatsapp()), anyString());
    }

    @Test
    @DisplayName("Deve retornar falso ao enviar lembrete quando paciente não tem WhatsApp")
    void retornarFalsoAoEnviarLembreteSemWhatsApp() {
        // Arrange
        // Configurar uma lista de telefones sem WhatsApp
        ReflectionTestUtils.setField(paciente, "telefones", new ArrayList<>());
        
        // Act
        boolean resultado = lembreteService.enviarLembreteImediato(consulta);
        
        // Assert
        assertFalse(resultado);
        verify(whatsappService, never()).enviarMensagemSimples(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar falso ao enviar lembrete quando ocorre exceção")
    void retornarFalsoAoEnviarLembreteComExcecao() {
        // Arrange
        when(whatsappService.enviarMensagemSimples(anyString(), anyString())).thenThrow(new RuntimeException("Erro simulado"));
        
        // Act
        boolean resultado = lembreteService.enviarLembreteImediato(consulta);
        
        // Assert
        assertFalse(resultado);
        verify(whatsappService).enviarMensagemSimples(anyString(), anyString());
    }
}
