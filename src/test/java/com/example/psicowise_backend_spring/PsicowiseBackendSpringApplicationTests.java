package com.example.psicowise_backend_spring;

import com.example.psicowise_backend_spring.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ImportAutoConfiguration
@ActiveProfiles("test")
@Import(TestConfig.class)

class PsicowiseBackendSpringApplicationTests {

	@Test
	void contextLoads() {
	}

}
