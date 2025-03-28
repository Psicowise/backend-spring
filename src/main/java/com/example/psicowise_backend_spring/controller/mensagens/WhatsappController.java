package com.example.psicowise_backend_spring.controller.mensagens;
import com.example.psicowise_backend_spring.dto.mensagens.WhatsappMensagemDto;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PSICOLOGO')")
public class WhatsappController {

    private final WhatsappService whatsappService;

    /**
     * Envia uma mensagem simples de texto via WhatsApp
     *
     * @param mensagemDto DTO contendo o número de telefone e a mensagem
     * @return Resposta de sucesso ou erro
     */
    @PostMapping("/enviar-mensagem-simples")
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
}
