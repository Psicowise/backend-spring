package com.example.psicowise_backend_spring.service.notificacao;

import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LembreteService {

    private final WhatsappService whatsappService;

    // Formatadores de data e hora
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Mapa para armazenar os lembretes agendados
    private final Map<UUID, List<LembreteAgendado>> lembretesAgendados = new ConcurrentHashMap<>();

    /**
     * Agenda os lembretes para uma consulta
     * @param consulta Consulta para a qual agendar lembretes
     */
    public void agendarLembretes(Consulta consulta) {
        if (consulta == null || consulta.getDataHora() == null || consulta.getId() == null) {
            log.error("Dados insuficientes para agendar lembretes");
            return;
        }

        log.info("Agendando lembretes para consulta ID: {}", consulta.getId());

        // Agendar lembretes com diferentes antecedências
        agendarLembrete(consulta, 72, TipoLembrete.TRES_DIAS);  // 3 dias antes
        agendarLembrete(consulta, 24, TipoLembrete.UM_DIA);     // 1 dia antes
        agendarLembrete(consulta, 1, TipoLembrete.UMA_HORA);    // 1 hora antes
    }

    /**
     * Verifica periodicamente os lembretes agendados e envia quando for hora
     */
    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos
    public void verificarLembretesAgendados() {
        LocalDateTime agora = LocalDateTime.now();
        log.debug("Verificando lembretes agendados em: {}", agora);

        lembretesAgendados.forEach((consultaId, lembretes) -> {
            List<LembreteAgendado> lembretesParaRemover = new ArrayList<>();

            for (LembreteAgendado lembrete : lembretes) {
                if (lembrete.getHoraEnvio().isBefore(agora)) {
                    log.info("Enviando lembrete agendado: {}", lembrete.getTipo());
                    enviarLembrete(lembrete);
                    lembretesParaRemover.add(lembrete);
                }
            }

            // Remover lembretes enviados
            if (!lembretesParaRemover.isEmpty()) {
                lembretes.removeAll(lembretesParaRemover);
                log.debug("Removidos {} lembretes já enviados", lembretesParaRemover.size());
            }
        });

        // Remover consultas sem lembretes pendentes
        lembretesAgendados.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private void agendarLembrete(Consulta consulta, int horasAntes, TipoLembrete tipo) {
        LocalDateTime horaConsulta = consulta.getDataHora();
        LocalDateTime horaEnvio = horaConsulta.minusHours(horasAntes);

        // Se a hora de envio já passou, não agendar
        if (horaEnvio.isBefore(LocalDateTime.now())) {
            log.debug("Não agendando lembrete tipo {} - hora de envio já passou", tipo);
            return;
        }

        LembreteAgendado lembrete = new LembreteAgendado(consulta, horaEnvio, tipo);

        // Adicionar à lista de lembretes para a consulta
        lembretesAgendados.computeIfAbsent(consulta.getId(), k -> new ArrayList<>())
                          .add(lembrete);

        log.debug("Lembrete agendado: tipo={}, consulta={}, envio={}", 
                  tipo, consulta.getId(), horaEnvio);
    }

    private void enviarLembrete(LembreteAgendado lembrete) {
        Consulta consulta = lembrete.getConsulta();

        try {
            if (consulta.getPaciente() == null) {
                log.error("Paciente não encontrado para a consulta: {}", consulta.getId());
                return;
            }

            String telefoneWhatsapp = consulta.getPaciente().getTelefoneWhatsapp();
            if (telefoneWhatsapp == null || telefoneWhatsapp.isBlank()) {
                log.error("Paciente sem telefone WhatsApp cadastrado: {}", consulta.getPaciente().getId());
                return;
            }

            String mensagem = criarMensagemLembrete(consulta, lembrete.getTipo());
            boolean enviado = whatsappService.enviarMensagemSimples(telefoneWhatsapp, mensagem);

            if (enviado) {
                log.info("Lembrete enviado com sucesso: tipo={}, paciente={}", 
                         lembrete.getTipo(), consulta.getPaciente().getId());
            } else {
                log.warn("Falha ao enviar lembrete: tipo={}, paciente={}", 
                         lembrete.getTipo(), consulta.getPaciente().getId());
            }
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete: {}", e.getMessage(), e);
        }
    }

    private String criarMensagemLembrete(Consulta consulta, TipoLembrete tipo) {
        String dataConsulta = consulta.getDataHora().format(DATE_FORMATTER);
        String horaConsulta = consulta.getDataHora().format(TIME_FORMATTER);
        String nomePaciente = consulta.getPaciente().getNome();
        String nomePsicologo = consulta.getPsicologo().getUsuario().getNome();

        switch (tipo) {
            case TRES_DIAS:
                return String.format(
                    "Olá %s, lembramos que você tem uma consulta com Dr(a). %s agendada para o dia %s às %s. Faltam 3 dias!",
                    nomePaciente, nomePsicologo, dataConsulta, horaConsulta
                );
            case UM_DIA:
                return String.format(
                    "Olá %s, sua consulta com Dr(a). %s será amanhã, %s às %s. Não esqueça!",
                    nomePaciente, nomePsicologo, dataConsulta, horaConsulta
                );
            case UMA_HORA:
                return String.format(
                    "Olá %s, sua consulta com Dr(a). %s começará em breve, às %s. Prepare-se!",
                    nomePaciente, nomePsicologo, horaConsulta
                );
            default:
                return String.format(
                    "Olá %s, este é um lembrete da sua consulta com Dr(a). %s agendada para o dia %s às %s. " +
                    "Caso precise reagendar, entre em contato conosco.",
                    nomePaciente, nomePsicologo, dataConsulta, horaConsulta
                );
        }
    }

    /**
     * Método para enviar lembrete imediatamente (caso necessário)
     */
    public boolean enviarLembreteImediato(Consulta consulta) {
        try {
            if (consulta.getPaciente() == null || consulta.getDataHora() == null) {
                log.error("Dados insuficientes para enviar lembrete imediato");
                return false;
            }

            String telefoneWhatsapp = consulta.getPaciente().getTelefoneWhatsapp();
            if (telefoneWhatsapp == null || telefoneWhatsapp.isBlank()) {
                log.error("Paciente sem telefone WhatsApp cadastrado");
                return false;
            }

            String mensagem = criarMensagemLembrete(consulta, TipoLembrete.PADRAO);
            return whatsappService.enviarMensagemSimples(telefoneWhatsapp, mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete imediato: {}", e.getMessage(), e);
            return false;
        }
    }

    // Classe interna para representar um lembrete agendado
    private static class LembreteAgendado {
        private final Consulta consulta;
        private final LocalDateTime horaEnvio;
        private final TipoLembrete tipo;

        public LembreteAgendado(Consulta consulta, LocalDateTime horaEnvio, TipoLembrete tipo) {
            this.consulta = consulta;
            this.horaEnvio = horaEnvio;
            this.tipo = tipo;
        }

        public Consulta getConsulta() {
            return consulta;
        }

        public LocalDateTime getHoraEnvio() {
            return horaEnvio;
        }

        public TipoLembrete getTipo() {
            return tipo;
        }
    }

    // Enum para os tipos de lembrete
    private enum TipoLembrete {
        TRES_DIAS,
        UM_DIA,
        UMA_HORA,
        PADRAO
    }
}