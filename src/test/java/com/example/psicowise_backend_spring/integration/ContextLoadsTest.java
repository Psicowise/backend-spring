package com.example.psicowise_backend_spring.integration;

import com.example.psicowise_backend_spring.controller.autenticacao.UsuarioAtualController;
import com.example.psicowise_backend_spring.util.TestEnvLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration
@ActiveProfiles("test")
public class ContextLoadsTest {

    @BeforeAll
    public static void setUp() {
        TestEnvLoader.init();
    }

    @Autowired(required = false)
    private UsuarioAtualController usuarioAtualController;

    @Test
    void controllerDeveSerInjetado() {
        assertThat(usuarioAtualController)
                .withFailMessage("⚠️ O controller não foi carregado pelo Spring Boot!")
                .isNotNull();
    }
}
