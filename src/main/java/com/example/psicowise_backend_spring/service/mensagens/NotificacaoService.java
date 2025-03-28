package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Serviço para envio de notificações aos pacientes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final WhatsappService whatsappService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Envia uma notificação de lembrete de consulta para o paciente
     *
     * @param consulta Consulta agendada
     * @return true se a notificação foi enviada com sucesso, false caso contrário
     */
    public boolean enviarLembreteConsulta(Consulta consulta) {
        try {
            if (consulta.getPaciente() == null || consulta.getDataHora() == null) {
                log.error("Dados insuficientes para enviar lembrete de consulta");
                return false;
            }

            String telefoneWhatsapp = consulta.getPaciente().getTelefoneWhatsapp();
            if (telefoneWhatsapp == null || telefoneWhatsapp.isBlank()) {
                log.error("Paciente sem telefone WhatsApp cadastrado");
                return false;
            }

            String mensagem = String.format(
                    "Olá %s, este é um lembrete da sua consulta com Dr(a). %s agendada para o dia %s às %s. " +
                            "Caso precise reagendar, entre em contato conosco.",
                    consulta.getPaciente().getNome(),
                    consulta.getPsicologo().getUsuario().getNome(),
                    consulta.getDataHora().format(DATE_FORMATTER),
                    consulta.getDataHora().format(TIME_FORMATTER)
            );

            return whatsappService.enviarMensagemSimples(telefoneWhatsapp, mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete de consulta: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Envia uma notificação de confirmação de agendamento para o paciente
     *
     * @param consulta Consulta agendada
     * @return true se a notificação foi enviada com sucesso, false caso contrário
     */
    public boolean enviarConfirmacaoAgendamento(Consulta consulta) {
        try {
            if (consulta.getPaciente() == null || consulta.getDataHora() == null) {
                log.error("Dados insuficientes para enviar confirmação de agendamento");
                return false;
            }

            String telefoneWhatsapp = consulta.getPaciente().getTelefoneWhatsapp();
            if (telefoneWhatsapp == null || telefoneWhatsapp.isBlank()) {
                log.error("Paciente sem telefone WhatsApp cadastrado");
                return false;
            }

            String mensagem = String.format(
                    "Olá %s, sua consulta com Dr(a). %s foi confirmada para o dia %s às %s. " +
                            "Agradecemos a preferência!",
                    consulta.getPaciente().getNome(),
                    consulta.getPsicologo().getUsuario().getNome(),
                    consulta.getDataHora().format(DATE_FORMATTER),
                    consulta.getDataHora().format(TIME_FORMATTER)
            );

            return whatsappService.enviarMensagemSimples(telefoneWhatsapp, mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar confirmação de agendamento: {}", e.getMessage(), e);
            return false;
        }
    }
}