package com.example.psicowise_backend_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PsicowiseBackendSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PsicowiseBackendSpringApplication.class, args);
	}

}
