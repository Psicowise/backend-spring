package com.example.psicowise_backend_spring;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsicowiseBackendSpringApplication {

	public static void main(String[] args) {

		if (System.getenv("SPRING_DATASOURCE_URL") == null) {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		}

		SpringApplication.run(PsicowiseBackendSpringApplication.class, args);
	}

}
