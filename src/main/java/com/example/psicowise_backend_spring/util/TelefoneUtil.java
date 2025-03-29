package com.example.psicowise_backend_spring.util;

import com.example.psicowise_backend_spring.entity.common.Telefone;
import com.example.psicowise_backend_spring.entity.consulta.Paciente;
import com.example.psicowise_backend_spring.entity.consulta.Psicologo;
import com.example.psicowise_backend_spring.enums.common.TipoProprietario;
import com.example.psicowise_backend_spring.enums.common.TipoTelefone;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Classe utilitária para manipulação de telefones
 */
public class TelefoneUtil {

    /**
     * Busca o telefone principal de uma lista de telefones
     *
     * @param telefones Lista de telefones
     * @return O telefone principal ou null se não existir
     */
    public static Telefone buscarTelefonePrincipal(List<Telefone> telefones) {
        if (telefones == null || telefones.isEmpty()) {
            return null;
        }

        return telefones.stream()
                .filter(Telefone::isPrincipal)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna o número formatado do telefone principal
     *
     * @param telefones Lista de telefones
     * @return Número do telefone principal formatado ou null
     */
    public static String obterNumeroTelefonePrincipal(List<Telefone> telefones) {
        Telefone principal = buscarTelefonePrincipal(telefones);
        if (principal == null) {
            return null;
        }

        // Formatação diretamente aqui em vez de chamar um método da classe Telefone
        return "(" + principal.getDdd() + ") " + principal.getNumero();
    }

    /**
     * Busca o telefone WhatsApp de uma lista de telefones
     * Prioriza telefones marcados como WhatsApp E principal
     *
     * @param telefones Lista de telefones
     * @return O telefone WhatsApp ou null se não existir
     */
    public static Telefone buscarTelefoneWhatsapp(List<Telefone> telefones) {
        if (telefones == null || telefones.isEmpty()) {
            return null;
        }

        // Primeiro procura um telefone que seja principal E WhatsApp
        Telefone telefone = telefones.stream()
                .filter(t -> t.isPrincipal() && t.isWhatsapp())
                .findFirst()
                .orElse(null);

        // Se não encontrou, procura qualquer telefone WhatsApp
        if (telefone == null) {
            telefone = telefones.stream()
                    .filter(Telefone::isWhatsapp)
                    .findFirst()
                    .orElse(null);
        }

        return telefone;
    }

    /**
     * Retorna o número formatado para WhatsApp
     *
     * @param telefones Lista de telefones
     * @return Número formatado para WhatsApp ou null
     */
    public static String obterNumeroWhatsapp(List<Telefone> telefones) {
        Telefone whatsapp = buscarTelefoneWhatsapp(telefones);
        return whatsapp != null ? whatsapp.getNumeroFormatadoWhatsapp() : null;
    }

    /**
     * Marca um telefone como principal e desmarca os demais
     *
     * @param telefones Lista de telefones
     * @param telefonePrincipal Telefone a ser marcado como principal
     */
    public static void definirTelefonePrincipal(List<Telefone> telefones, Telefone telefonePrincipal) {
        if (telefones == null || telefones.isEmpty()) {
            return;
        }

        telefones.forEach(t -> t.setPrincipal(false));
        if (telefonePrincipal != null) {
            telefonePrincipal.setPrincipal(true);
        }
    }

    /**
     * Cria um novo telefone
     *
     * @param numero Número do telefone
     * @param ddd DDD
     * @param codigoPais Código do país
     * @param tipo Tipo do telefone
     * @param principal Se é o telefone principal
     * @param whatsapp Se é um número WhatsApp
     * @param proprietarioId ID do proprietário do telefone
     * @param tipoProprietario Tipo do proprietário
     * @return O telefone criado
     */
    public static Telefone criarTelefone(String numero, String ddd, String codigoPais,
                                         TipoTelefone tipo, boolean principal, boolean whatsapp,
                                         UUID proprietarioId, TipoProprietario tipoProprietario) {
        Telefone telefone = new Telefone();
        telefone.setNumero(numero);
        telefone.setDdd(ddd);
        telefone.setCodigoPais(codigoPais);
        telefone.setTipo(tipo);
        telefone.setPrincipal(principal);
        telefone.setWhatsapp(whatsapp);
        telefone.setProprietarioId(proprietarioId);
        telefone.setTipoProprietario(tipoProprietario);
        return telefone;
    }

    /**
     * Adiciona um telefone à lista de telefones
     *
     * @param telefones Lista de telefones
     * @param telefone Telefone a ser adicionado
     */
    public static void adicionarTelefone(List<Telefone> telefones, Telefone telefone) {
        if (telefones == null || telefone == null) {
            return;
        }

        // Se for principal, desmarca os outros
        if (telefone.isPrincipal()) {
            telefones.forEach(t -> {
                if (t.getProprietarioId().equals(telefone.getProprietarioId()) &&
                        t.getTipoProprietario() == telefone.getTipoProprietario()) {
                    t.setPrincipal(false);
                }
            });
        }

        // Adiciona à lista de telefones
        telefones.add(telefone);
    }

    /**
     * Adiciona um telefone ao paciente (método de compatibilidade)
     *
     * @param paciente Paciente
     * @param telefone Telefone a ser adicionado
     */
    public static void adicionarTelefonePaciente(Paciente paciente, Telefone telefone) {
        if (paciente == null || telefone == null) {
            return;
        }

        // Configurar o telefone para o paciente
        telefone.setProprietarioId(paciente.getId());
        telefone.setTipoProprietario(TipoProprietario.PACIENTE);

        // Se for principal, desmarca os outros
        if (telefone.isPrincipal()) {
            paciente.getTelefones().forEach(t -> t.setPrincipal(false));
        }

        // Adiciona à lista de telefones do paciente
        paciente.getTelefones().add(telefone);
    }

    /**
     * Adiciona um telefone ao psicólogo (método de compatibilidade)
     *
     * @param psicologo Psicólogo
     * @param telefone Telefone a ser adicionado
     */
    public static void adicionarTelefonePsicologo(Psicologo psicologo, Telefone telefone) {
        if (psicologo == null || telefone == null) {
            return;
        }

        // Configurar o telefone para o psicólogo
        telefone.setProprietarioId(psicologo.getId());
        telefone.setTipoProprietario(TipoProprietario.PSICOLOGO);

        // Se for principal, desmarca os outros
        if (telefone.isPrincipal()) {
            psicologo.getTelefones().forEach(t -> t.setPrincipal(false));
        }

        // Adiciona à lista de telefones do psicólogo
        psicologo.getTelefones().add(telefone);
    }

    /**
     * Remove um telefone da lista pelo ID
     *
     * @param telefones Lista de telefones
     * @param telefoneId ID do telefone a ser removido
     * @return true se o telefone foi removido, false caso contrário
     */
    public static boolean removerTelefone(List<Telefone> telefones, UUID telefoneId) {
        if (telefones == null || telefoneId == null) {
            return false;
        }

        // Busca o telefone pelo ID
        Telefone telefone = telefones.stream()
                .filter(t -> t.getId().equals(telefoneId))
                .findFirst()
                .orElse(null);

        if (telefone != null) {
            telefones.remove(telefone);
            return true;
        }

        return false;
    }

    /**
     * Remove um telefone do paciente (método de compatibilidade)
     *
     * @param paciente Paciente
     * @param telefoneId ID do telefone a ser removido
     * @return true se o telefone foi removido, false caso contrário
     */
    public static boolean removerTelefonePaciente(Paciente paciente, UUID telefoneId) {
        if (paciente == null || telefoneId == null) {
            return false;
        }

        return removerTelefone(paciente.getTelefones(), telefoneId);
    }

    /**
     * Remove um telefone do psicólogo (método de compatibilidade)
     *
     * @param psicologo Psicólogo
     * @param telefoneId ID do telefone a ser removido
     * @return true se o telefone foi removido, false caso contrário
     */
    public static boolean removerTelefonePsicologo(Psicologo psicologo, UUID telefoneId) {
        if (psicologo == null || telefoneId == null) {
            return false;
        }

        return removerTelefone(psicologo.getTelefones(), telefoneId);
    }

    /**
     * Filtra telefones por proprietário
     *
     * @param telefones Lista de telefones
     * @param proprietarioId ID do proprietário
     * @param tipoProprietario Tipo do proprietário
     * @return Lista de telefones do proprietário
     */
    public static List<Telefone> filtrarTelefonesPorProprietario(
            List<Telefone> telefones, UUID proprietarioId, TipoProprietario tipoProprietario) {
        if (telefones == null || proprietarioId == null) {
            return List.of();
        }

        return telefones.stream()
                .filter(t -> t.getProprietarioId().equals(proprietarioId) &&
                        t.getTipoProprietario() == tipoProprietario)
                .collect(Collectors.toList());
    }
}