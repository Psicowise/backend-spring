package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.service.mensagens.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoLembreteServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private AgendamentoLembreteService agendamentoLembreteService;

    private Consulta consulta1;
    private Consulta consulta2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        consulta1 = new Consulta();
        consulta1.setDataHora(now.plusHours(24));

        consulta2 = new Consulta();
        consulta2.setDataHora(now.plusHours(1));
    }

    @Test
    @DisplayName("Deve enviar lembretes para consultas agendadas")
    void testEnviarLembretes() {
        // Arrange
        List<Consulta> consultas = Arrays.asList(consulta1, consulta2);
        when(consultaRepository.findByDataHoraBetween(any(), any())).thenReturn(consultas);
        when(notificacaoService.enviarLembreteConsulta(any(Consulta.class))).thenReturn(true);

        // Act
        agendamentoLembreteService.enviarLembretes();

        // Assert
        verify(consultaRepository).findByDataHoraBetween(any(), any());
        verify(notificacaoService, times(2)).enviarLembreteConsulta(any(Consulta.class));
    }

    @Test
    @DisplayName("Não deve enviar lembretes quando não há consultas")
    void testNaoEnviarLembretesQuandoNaoHaConsultas() {
        // Arrange
        when(consultaRepository.findByDataHoraBetween(any(), any())).thenReturn(List.of());

        // Act
        agendamentoLembreteService.enviarLembretes();

        // Assert
        verify(consultaRepository).findByDataHoraBetween(any(), any());
        verify(notificacaoService, never()).enviarLembreteConsulta(any(Consulta.class));
    }
}