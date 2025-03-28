package com.example.psicowise_backend_spring.service.mensagens.whatsapp.impl;

import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementação do serviço de WhatsApp usando a Z-API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ZapiWhatsappService implements WhatsappService {

    private final RestTemplate restTemplate;

    @Value("${zapi.instance}")
    private String instanceId;

    @Value("${zapi.token}")
    private String apiToken;

    private static final String Z_API_BASE_URL = "https://api.z-api.io/instances/";

    @Override
    public boolean enviarMensagemSimples(String phoneNumber, String message) {
        try {
            String url = Z_API_BASE_URL + instanceId + "/token/" + apiToken + "/send-text";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("phone", phoneNumber);
            requestBody.put("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem WhatsApp: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean enviarMensagemImagem(String phoneNumber, String message, MultipartFile imageFile) {
        try {
            String url = Z_API_BASE_URL + instanceId + "/token/" + apiToken + "/send-image";

            // Converter imagem para base64
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("phone", phoneNumber);
            requestBody.put("image", base64Image);

            if (message != null && !message.isEmpty()) {
                requestBody.put("caption", message);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem com imagem via WhatsApp: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean enviarMensagemAnexo(String phoneNumber, String message, MultipartFile file, String fileName) {
        try {
            String url = Z_API_BASE_URL + instanceId + "/token/" + apiToken + "/send-document";

            // Converter arquivo para base64
            String base64File = Base64.getEncoder().encodeToString(file.getBytes());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("phone", phoneNumber);
            requestBody.put("document", base64File);
            requestBody.put("fileName", fileName);

            if (message != null && !message.isEmpty()) {
                requestBody.put("caption", message);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem com anexo via WhatsApp: {}", e.getMessage(), e);
            return false;
        }
    }
}