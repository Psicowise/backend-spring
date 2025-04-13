package com.example.psicowise_backend_spring.service.consultas;

import com.example.psicowise_backend_spring.dto.consultas.SalaVideoDto;
import com.example.psicowise_backend_spring.entity.consulta.Consulta;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.entity.consulta.SalaVideo;
import com.example.psicowise_backend_spring.repository.consulta.ConsultaRepository;
import com.example.psicowise_backend_spring.repository.consulta.PsicologoRepository;
import com.example.psicowise_backend_spring.repository.consulta.SalaVideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SalaVideoService {

    private final SalaVideoRepository salaVideoRepository;
    private final ConsultaRepository consultaRepository;
    private final PsicologoRepository psicologoRepository;

    @Value("${app.webrtc.server:https://meet.jit.si}")
    private String webrtcServer;

    public SalaVideoService(
            SalaVideoRepository salaVideoRepository,
            ConsultaRepository consultaRepository,
            PsicologoRepository psicologoRepository) {
        this.salaVideoRepository = salaVideoRepository;
        this.consultaRepository = consultaRepository;
        this.psicologoRepository = psicologoRepository;
    }

    @Transactional
    public ResponseEntity<SalaVideoDto> criarSalaVideo(UUID consultaId) {
        try {
            Consulta consulta = consultaRepository.findById(consultaId)
                    .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

            Psicologo psicologoAutenticado = getPsicologoAutenticado();
            if (!consulta.getPsicologo().getId().equals(psicologoAutenticado.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            return salaVideoRepository.findByConsulta(consulta)
                    .map(sala -> ResponseEntity.ok(converterParaDto(sala)))
                    .orElseGet(() -> {
                        SalaVideo novaSala = criarNovaSala(consulta);
                        return ResponseEntity.ok(converterParaDto(novaSala));
                    });

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private SalaVideo criarNovaSala(Consulta consulta) {
        SalaVideo sala = new SalaVideo();
        sala.setConsulta(consulta);
        String salaId = "psicowise_" + consulta.getId().toString().substring(0, 8);
        sala.setSalaId(salaId);
        sala.setLinkAcesso(webrtcServer + "/" + salaId);
        sala.setLinkHost(webrtcServer + "/" + salaId + "#config.startWithVideoMuted=false");
        sala.setAtiva(false);
        return salaVideoRepository.save(sala);
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
            String usuarioIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
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


    private Psicologo getPsicologoAutenticado() {
        String usuarioId = SecurityContextHolder.getContext().getAuthentication().getName();
        return psicologoRepository.findByUsuarioId(UUID.fromString(usuarioId))
                .orElseThrow(() -> new RuntimeException("Psicólogo não encontrado"));
    }

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