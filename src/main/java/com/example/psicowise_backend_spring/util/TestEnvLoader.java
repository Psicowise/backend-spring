package com.example.psicowise_backend_spring.util;

import io.github.cdimascio.dotenv.Dotenv;

public class TestEnvLoader {
    static {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    public static void init() {
        // Só força o carregamento do bloco static
    }
}