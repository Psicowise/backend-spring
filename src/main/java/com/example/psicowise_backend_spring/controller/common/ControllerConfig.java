package com.example.psicowise_backend_spring.controller.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.psicowise_backend_spring.controller")
public class ControllerConfig {
}