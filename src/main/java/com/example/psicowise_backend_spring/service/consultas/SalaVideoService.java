package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.dto.consultas.SalaVideoDto;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SalaVideoService {

    private final SalaVideoRepository salaVideoRepository;
    private final ConsultaRepository consultaRepository;
    private final PsicologoRepository psicologoRepository;

    @Value("${app.webrtc.server:https://meet.jit.si}")
    private String webrtcServer;

    @Autowired
    public SalaVideoService(
            SalaVideoRepository salaVideoRepository,
            ConsultaRepository consultaRepository,
            PsicologoRepository psicologoRepository) {
        this.salaVideoRepository = salaVideoRepository;
        this.consultaRepository = consultaRepository;
        this.psicologoRepository = psicologoRepository;
    }

    /**
     * Cria uma sala de vídeo para uma consulta
     *
     * @param consultaId ID da consulta
     * @return DTO com os dados da sala criada
     */
    @Transactional
    public ResponseEntity<SalaVideoDto> criarSalaVideo(UUID consultaId) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Verificar se já existe uma sala para esta consulta
            Optional<SalaVideo> salaExistente = salaVideoRepository.findByConsulta(consulta);
            if (salaExistente.isPresent()) {
                return ResponseEntity.ok(converterParaDto(salaExistente.get()));
            }

            // Gerar ID único para a sala
            String salaId = "psicowise_" + gerarIdAleatorio();

            // Criar links de acesso
            String linkAcesso = webrtcServer + "/" + salaId;
            String linkHost = linkAcesso + "#config.startWithVideoMuted=false" +
                    "&config.startWithAudioMuted=false" +
                    "&interfaceConfig.DISABLE_JOIN_LEAVE_NOTIFICATIONS=true" +
                    "&interfaceConfig.TOOLBAR_BUTTONS=[\"microphone\",\"camera\",\"closedcaptions\",\"desktop\",\"fullscreen\",\"fodeviceselection\",\"hangup\",\"profile\",\"chat\",\"recording\",\"livestreaming\",\"etherpad\",\"sharedvideo\",\"settings\",\"raisehand\",\"videoquality\",\"filmstrip\",\"invite\",\"feedback\",\"stats\",\"shortcuts\",\"tileview\",\"videobackgroundblur\",\"download\",\"help\",\"mute-everyone\"]";

            // Criar a sala
            SalaVideo sala = new SalaVideo();
            sala.setConsulta(consulta);
            sala.setSalaId(salaId);
            sala.setLinkAcesso(linkAcesso);
            sala.setLinkHost(linkHost);
            sala.setAtiva(false); // A sala será ativada mais tarde, próximo ao horário da consulta

            SalaVideo salaSalva = salaVideoRepository.save(sala);

            // Converter para DTO e retornar
            return ResponseEntity.ok(converterParaDto(salaSalva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Ativa uma sala de vídeo
     *
     * @param salaId ID da sala
     * @return Mensagem de sucesso ou erro
     */
    @Transactional
    public ResponseEntity<String> ativarSala(UUID salaId) {
        try {
            // Buscar a sala
            SalaVideo sala = salaVideoRepository.findById(salaId)
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!sala.getConsulta().getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            // Ativar a sala
            sala.setAtiva(true);
            sala.setDataAtivacao(LocalDateTime.now());
            salaVideoRepository.save(sala);

            return ResponseEntity.ok("Sala ativada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao ativar sala: " + e.getMessage());
        }
    }

    /**
     * Desativa uma sala de vídeo
     *
     * @param salaId ID da sala
     * @return Mensagem de sucesso ou erro
     */
    @Transactional
    public ResponseEntity<String> desativarSala(UUID salaId) {
        try {
            // Buscar a sala
            SalaVideo sala = salaVideoRepository.findById(salaId)
                    .orElseThrow(() -> new RuntimeException("Sala não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta
            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!sala.getConsulta().getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Acesso negado: você não é o psicólogo responsável por esta consulta");
            }

            // Desativar a sala
            sala.setAtiva(false);
            sala.setDataDesativacao(LocalDateTime.now());
            salaVideoRepository.save(sala);

            return ResponseEntity.ok("Sala desativada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao desativar sala: " + e.getMessage());
        }
    }

    /**
     * Obtém informações da sala de vídeo de uma consulta
     *
     * @param consultaId ID da consulta
     * @return DTO com os dados da sala
     */
    public ResponseEntity<SalaVideoDto> obterSalaVideo(UUID consultaId) {
        try {
            // Verificar se a consulta existe
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            // Verificar se o psicólogo autenticado é responsável pela consulta ou se é o paciente da consulta
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String usuarioIdStr = auth.getName();
            UUID usuarioId = UUID.fromString(usuarioIdStr);

            Psicologo psicologoAutenticado = psicologoRepository.findByUsuarioId(usuarioId).orElse(null);
            boolean isPsicologo = psicologoAutenticado != null &&
                    consulta.getPsicologo().getId().equals(psicologoAutenticado.getId());

            boolean isPaciente = consulta.getPaciente().getId().equals(usuarioId);

            if (!isPsicologo && !isPaciente) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Buscar a sala
            Optional<SalaVideo> salaOpt = salaVideoRepository.findByConsulta(consulta);
            if (salaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Converter para DTO e retornar
            return ResponseEntity.ok(converterParaDto(salaOpt.get()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Gera um ID aleatório para a sala
     *
     * @return String com o ID gerado
     */
    private String gerarIdAleatorio() {
        // Combinar UUID com timestamp para garantir unicidade
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12) +
                "_" + System.currentTimeMillis() % 10000;
    }

    /**
     * Obtém o psicólogo autenticado
     *
     * @return O psicólogo autenticado
     */
    private Psicologo getPsicologoAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idString = auth.getName();
        UUID usuarioId = UUID.fromString(idString);

        return psicologoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Psicólogo não encontrado"));
    }

    /**
     * Converte uma entidade SalaVideo para DTO
     *
     * @param sala Entidade a ser convertida
     * @return DTO correspondente
     */
    private SalaVideoDto converterParaDto(SalaVideo sala) {
        return new SalaVideoDto(
                sala.getId(),
                sala.getConsulta().getId(),
                sala.getSalaId(),
                sala.getLinkAcesso(),
                sala.getLinkHost(),
                sala.isAtiva(),
                sala.getDataAtivacao(),
                sala.getDataDesativacao()
        );
    }
}