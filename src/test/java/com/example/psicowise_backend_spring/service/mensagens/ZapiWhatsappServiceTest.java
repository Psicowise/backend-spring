package com.example.psicowise_backend_spring.service.mensagens;

import com.example.psicowise_backend_spring.service.mensagens.whatsapp.WhatsappService;
import com.example.psicowise_backend_spring.service.mensagens.whatsapp.impl.ZapiWhatsappService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZapiWhatsappServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ZapiWhatsappService whatsappService;

    private final String INSTANCE_ID = "test-instance-123";
    private final String API_TOKEN = "test-token-456";
    private final String PHONE_NUMBER = "5511999999999";
    private final String MESSAGE = "Test message";

    @BeforeEach
    void setUp() {
        // Configurar valores para as propriedades necessárias usando ReflectionTestUtils
        ReflectionTestUtils.setField(whatsappService, "instanceId", INSTANCE_ID);
        ReflectionTestUtils.setField(whatsappService, "apiToken", API_TOKEN);
    }

    @Test
    @DisplayName("Deve enviar mensagem de texto com sucesso")
    void testEnviarMensagemSimplesSucesso() {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-text";

        // Mock da resposta do RestTemplate
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // Act
        boolean resultado = whatsappService.enviarMensagemSimples(PHONE_NUMBER, MESSAGE);

        // Assert
        assertTrue(resultado);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Deve retornar falso quando falhar ao enviar mensagem de texto")
    void testEnviarMensagemSimplesFalha() {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-text";

        // Mock da falha no RestTemplate
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Erro de comunicação"));

        // Act
        boolean resultado = whatsappService.enviarMensagemSimples(PHONE_NUMBER, MESSAGE);

        // Assert
        assertFalse(resultado);
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Deve enviar mensagem com imagem com sucesso")
    void testEnviarMensagemImagemSucesso() throws Exception {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-image";

        // Mock do MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] fileContent = "test image content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);

        // Mock da resposta do RestTemplate
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // Act
        boolean resultado = whatsappService.enviarMensagemImagem(PHONE_NUMBER, MESSAGE, mockFile);

        // Assert
        assertTrue(resultado);
        verify(mockFile, times(1)).getBytes();
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Deve retornar falso quando falhar ao enviar mensagem com imagem")
    void testEnviarMensagemImagemFalha() throws Exception {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-image";

        // Mock do MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] fileContent = "test image content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);

        // Mock da falha no RestTemplate
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Erro de comunicação"));

        // Act
        boolean resultado = whatsappService.enviarMensagemImagem(PHONE_NUMBER, MESSAGE, mockFile);

        // Assert
        assertFalse(resultado);
        verify(mockFile, times(1)).getBytes();
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Deve enviar mensagem com anexo com sucesso")
    void testEnviarMensagemAnexoSucesso() throws Exception {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-document";
        String fileName = "test-document.pdf";

        // Mock do MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] fileContent = "test document content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);

        // Mock da resposta do RestTemplate
        ResponseEntity<Map> mockResponse = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(mockResponse);

        // Act
        boolean resultado = whatsappService.enviarMensagemAnexo(PHONE_NUMBER, MESSAGE, mockFile, fileName);

        // Assert
        assertTrue(resultado);
        verify(mockFile, times(1)).getBytes();
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Deve retornar falso quando falhar ao enviar mensagem com anexo")
    void testEnviarMensagemAnexoFalha() throws Exception {
        // Arrange
        String url = "https://api.z-api.io/instances/" + INSTANCE_ID + "/token/" + API_TOKEN + "/send-document";
        String fileName = "test-document.pdf";

        // Mock do MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        byte[] fileContent = "test document content".getBytes();
        when(mockFile.getBytes()).thenReturn(fileContent);

        // Mock da falha no RestTemplate
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("Erro de comunicação"));

        // Act
        boolean resultado = whatsappService.enviarMensagemAnexo(PHONE_NUMBER, MESSAGE, mockFile, fileName);

        // Assert
        assertFalse(resultado);
        verify(mockFile, times(1)).getBytes();
        verify(restTemplate, times(1)).exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }
}