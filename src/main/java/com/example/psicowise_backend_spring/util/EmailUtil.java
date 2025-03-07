package com.example.psicowise_backend_spring.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    /**
     * Envia um email simples (texto plano)
     *
     * @param to      Destinatário
     * @param subject Assunto
     * @param text    Conteúdo do email
     */
    public void enviarEmailSimples(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    /**
     * Envia um email com conteúdo HTML
     *
     * @param to      Destinatário
     * @param subject Assunto
     * @param html    Conteúdo HTML do email
     */
    public void enviarEmailHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        emailSender.send(message);
    }

    /**
     * Envia um email com um ou mais anexos
     *
     * @param to        Destinatário
     * @param subject   Assunto
     * @param text      Conteúdo do email
     * @param isHtml    Se o conteúdo é HTML
     * @param anexos    Mapa com nome do anexo e arquivo
     */
    public void enviarEmailComAnexos(String to, String subject, String text, boolean isHtml,
                                     Map<String, File> anexos) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, isHtml);

        // Adicionar os anexos
        if (anexos != null && !anexos.isEmpty()) {
            for (Map.Entry<String, File> anexo : anexos.entrySet()) {
                FileSystemResource file = new FileSystemResource(anexo.getValue());
                helper.addAttachment(anexo.getKey(), file);
            }
        }

        emailSender.send(message);
    }

    /**
     * Envia um email usando um template Thymeleaf
     *
     * @param to         Destinatário
     * @param subject    Assunto
     * @param template   Nome do template (sem extensão)
     * @param parameters Parâmetros para o template
     */
    public void enviarEmailTemplate(String to, String subject, String template,
                                    Map<String, Object> parameters) throws MessagingException {
        // Criar contexto Thymeleaf com os parâmetros
        Context context = new Context();
        if (parameters != null) {
            parameters.forEach(context::setVariable);
        }

        // Processar o template
        String htmlContent = templateEngine.process(template, context);

        // Enviar email com o conteúdo HTML
        enviarEmailHtml(to, subject, htmlContent);
    }
}
