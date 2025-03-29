package com.example.psicowise_backend_spring.service.common;

import com.example.psicowise_backend_spring.dto.common.CriarTelefoneDto;
import com.example.psicowise_backend_spring.dto.common.TelefoneDto;
import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.repository.common.TelefoneRepository;
import com.example.psicowise_backend_spring.service.autenticacao.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelefoneService {

    private final TelefoneRepository telefoneRepository;
    private final UsuarioService usuarioService;

    /**
     * Cria um novo telefone
     *
     * @param dto DTO com os dados do telefone
     * @return O telefone criado
     */
    @Transactional
    public ResponseEntity<TelefoneDto> criarTelefone(CriarTelefoneDto dto) {
        try {
            Telefone telefone = new Telefone();
            telefone.setNumero(dto.numero());
            telefone.setDdd(dto.ddd());
            telefone.setCodigoPais(dto.codigoPais());
            telefone.setTipo(dto.tipo());
            telefone.setPrincipal(dto.principal());
            telefone.setWhatsapp(dto.whatsapp());
            telefone.setObservacao(dto.observacao());
            telefone.setProprietarioId(dto.proprietarioId());
            telefone.setTipoProprietario(dto.tipoProprietario());

            // Se for definido como principal, desativar outros telefones principais do mesmo proprietário
            if (dto.principal()) {
                atualizarTelefonePrincipal(dto.proprietarioId(), dto.tipoProprietario());
            }

            // Salvar o telefone
            Telefone telefoneSalvo = telefoneRepository.save(telefone);
            return ResponseEntity.ok(converterParaDto(telefoneSalvo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Atualiza um telefone existente
     *
     * @param id ID do telefone
     * @param dto DTO com os novos dados
     * @return O telefone atualizado
     */
    @Transactional
    public ResponseEntity<TelefoneDto> atualizarTelefone(UUID id, CriarTelefoneDto dto) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            telefone.setNumero(dto.numero());
            telefone.setDdd(dto.ddd());
            telefone.setCodigoPais(dto.codigoPais());
            telefone.setTipo(dto.tipo());
            telefone.setWhatsapp(dto.whatsapp());
            telefone.setObservacao(dto.observacao());

            // Não permitimos alterar o proprietário do telefone
            // Se quiser mudar o proprietário, deve-se excluir e criar um novo

            // Atualizar o status de principal apenas se tiver mudado
            if (dto.principal() != telefone.isPrincipal()) {
                telefone.setPrincipal(dto.principal());

                // Se for definido como principal, desativar outros telefones principais
                if (dto.principal()) {
                    atualizarTelefonePrincipal(telefone.getProprietarioId(), telefone.getTipoProprietario());
                }
            }

            Telefone telefoneSalvo = telefoneRepository.save(telefone);
            return ResponseEntity.ok(converterParaDto(telefoneSalvo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Exclui um telefone
     *
     * @param id ID do telefone
     * @return Mensagem de sucesso
     */
    @Transactional
    public ResponseEntity<String> excluirTelefone(UUID id) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            telefoneRepository.delete(telefone);
            return ResponseEntity.ok("Telefone excluído com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir telefone: " + e.getMessage());
        }
    }

    /**
     * Busca um telefone pelo ID
     *
     * @param id ID do telefone
     * @return O telefone encontrado
     */
    public ResponseEntity<TelefoneDto> buscarTelefonePorId(UUID id) {
        try {
            Telefone telefone = telefoneRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Telefone não encontrado"));

            return ResponseEntity.ok(converterParaDto(telefone));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Lista todos os telefones de um proprietário
     *
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     * @return Lista de telefones
     */
    public ResponseEntity<List<TelefoneDto>> listarTelefonesPorProprietario(
            UUID proprietarioId, TipoProprietario tipoProprietario) {
        try {
            List<Telefone> telefones = telefoneRepository.findByProprietarioIdAndTipoProprietario(
                    proprietarioId, tipoProprietario);

            List<TelefoneDto> telefonesDto = telefones.stream()
                    .map(this::converterParaDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(telefonesDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtém o telefone WhatsApp principal de um proprietário
     *
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     * @return O número de telefone formatado para WhatsApp ou null
     */
    public String obterTelefoneWhatsapp(UUID proprietarioId, TipoProprietario tipoProprietario) {
        List<Telefone> telefones = telefoneRepository.findByProprietarioIdAndTipoProprietarioAndWhatsapp(
                proprietarioId, tipoProprietario, true);

        if (telefones.isEmpty()) {
            return null;
        }

        // Primeiramente, tentar encontrar um telefone que seja marcado como principal
        Telefone telefonePrincipal = telefones.stream()
                .filter(Telefone::isPrincipal)
                .findFirst()
                .orElse(telefones.get(0)); // Se não houver principal, pegar o primeiro

        return telefonePrincipal.getNumeroFormatadoWhatsapp();
    }

    /**
     * Atualiza o status de principal dos telefones de um proprietário
     *
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     */
    private void atualizarTelefonePrincipal(UUID proprietarioId, TipoProprietario tipoProprietario) {
        List<Telefone> telefones = telefoneRepository.findByProprietarioIdAndTipoProprietario(
                proprietarioId, tipoProprietario);

        telefones.forEach(tel -> {
            tel.setPrincipal(false);
            telefoneRepository.save(tel);
        });
    }

    /**
     * Converte uma entidade Telefone para DTO
     *
     * @param telefone Entidade a ser convertida
     * @return DTO correspondente
     */
    private TelefoneDto converterParaDto(Telefone telefone) {
        return new TelefoneDto(
                telefone.getId(),
                telefone.getNumero(),
                telefone.getDdd(),
                telefone.getCodigoPais(),
                telefone.getTipo(),
                telefone.isPrincipal(),
                telefone.isWhatsapp(),
                telefone.getObservacao(),
                telefone.getProprietarioId(),
                telefone.getTipoProprietario(),
                telefone.getNumeroFormatado(),
                telefone.getNumeroFormatadoWhatsapp()
        );
    }
}