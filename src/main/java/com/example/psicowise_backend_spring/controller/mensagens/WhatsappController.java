package com.example.psicowise_backend_spring.controller.mensagens;
import com.example.psicowise_backend_spring.dto.mensagens.WhatsappMensagemDto;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('PSICOLOGO')")
public class WhatsappController {

    private final RestTemplate restTemplate;

    @Value("${zapi.instance}")
    private String instanceId;

    @Value("${zapi.token}")
    private String apiToken;

    private static final String Z_API_BASE_URL = "https://api.z-api.io/instances/";

    private final WhatsappService whatsappService;

    /**
     * Envia uma mensagem simples de texto via WhatsApp
     *
     * @param mensagemDto DTO contendo o número de telefone e a mensagem
     * @return Resposta de sucesso ou erro
     */
    @PostMapping(value = "/enviar-mensagem-simples", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> enviarMensagemSimples(@RequestBody WhatsappMensagemDto mensagemDto) {
        boolean enviado = whatsappService.enviarMensagemSimples(
                mensagemDto.phoneNumber(),
                mensagemDto.message()
        );

        if (enviado) {
            return ResponseEntity.ok("Mensagem enviada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Falha ao enviar mensagem");
        }
    }

    /**
     * Envia uma mensagem com imagem via WhatsApp
     *
     * @param phoneNumber Número de telefone do destinatário
     * @param message     Mensagem de texto (opcional)
     * @param imageFile   Arquivo de imagem
     * @return Resposta de sucesso ou erro
     */
    @PostMapping("/enviar-mensagem-imagem")
    public ResponseEntity<String> enviarMensagemImagem(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam("imageFile") MultipartFile imageFile) {

        boolean enviado = whatsappService.enviarMensagemImagem(phoneNumber, message, imageFile);

        if (enviado) {
            return ResponseEntity.ok("Mensagem com imagem enviada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Falha ao enviar mensagem com imagem");
        }
    }

    /**
     * Envia uma mensagem com anexo via WhatsApp
     *
     * @param phoneNumber Número de telefone do destinatário
     * @param message     Mensagem de texto (opcional)
     * @param file        Arquivo a ser enviado
     * @param fileName    Nome do arquivo
     * @return Resposta de sucesso ou erro
     */
    @PostMapping("/enviar-mensagem-anexo")
    public ResponseEntity<String> enviarMensagemAnexo(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) {

        boolean enviado = whatsappService.enviarMensagemAnexo(phoneNumber, message, file, fileName);

        if (enviado) {
            return ResponseEntity.ok("Mensagem com anexo enviada com sucesso");
        } else {
            return ResponseEntity.badRequest().body("Falha ao enviar mensagem com anexo");
        }
    }

    @GetMapping("/verificar-status")
    public ResponseEntity<Map<String, Object>> verificarStatusZAPI() {
        try {
            // Para verificar se a instância está ativa
            String url = Z_API_BASE_URL + instanceId + "/token/" + apiToken + "/status";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            Map<String, Object> resultado = new HashMap<>();
            resultado.put("status", response.getStatusCode().value());
            resultado.put("resposta", response.getBody());

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("error", e.getClass().getSimpleName());
            erro.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }
}
