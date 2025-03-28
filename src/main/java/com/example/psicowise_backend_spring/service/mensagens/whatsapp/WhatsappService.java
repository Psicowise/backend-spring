package com.example.psicowise_backend_spring.service.mensagens.whatsapp;

import org.springframework.web.multipart.MultipartFile;

/**
 * Interface para serviços de integração com WhatsApp
 */
public interface WhatsappService {

    /**
     * Envia uma mensagem simples de texto via WhatsApp
     *
     * @param phoneNumber O número de telefone do destinatário (com código do país, sem caracteres especiais)
     * @param message     O texto da mensagem
     * @return true se a mensagem foi enviada com sucesso, false caso contrário
     */
    boolean enviarMensagemSimples(String phoneNumber, String message);

    /**
     * Envia uma mensagem com imagem via WhatsApp
     *
     * @param phoneNumber O número de telefone do destinatário (com código do país, sem caracteres especiais)
     * @param message     O texto da mensagem (opcional, pode ser null)
     * @param imageFile   O arquivo de imagem para enviar
     * @return true se a mensagem foi enviada com sucesso, false caso contrário
     */
    boolean enviarMensagemImagem(String phoneNumber, String message, MultipartFile imageFile);

    /**
     * Envia uma mensagem com um arquivo anexo via WhatsApp
     *
     * @param phoneNumber O número de telefone do destinatário (com código do país, sem caracteres especiais)
     * @param message     O texto da mensagem (opcional, pode ser null)
     * @param file        O arquivo para enviar
     * @param fileName    O nome do arquivo
     * @return true se a mensagem foi enviada com sucesso, false caso contrário
     */
    boolean enviarMensagemAnexo(String phoneNumber, String message, MultipartFile file, String fileName);
}