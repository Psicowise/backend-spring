    package com.example.psicowise_backend_spring.config;

    import com.example.psicowise_backend_spring.service.autenticacao.RecuperacaoSenhaService;
    import com.example.psicowise_backend_spring.util.EmailUtil;
    import com.example.psicowise_backend_spring.util.HashUtil;
    import com.example.psicowise_backend_spring.util.JwtUtil;
    import org.mockito.Mockito;
    import org.springframework.boot.test.context.TestConfiguration;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Primary;
    import org.springframework.context.annotation.Profile;
    import org.springframework.jdbc.datasource.DriverManagerDataSource;
    import org.springframework.mail.javamail.JavaMailSender;

    @TestConfiguration
    @Profile("test")
    public class TestConfig {

        @Bean
        @Primary
        public DriverManagerDataSource dataSource() {
            // Configuração de banco de dados em memória para testes
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            dataSource.setUsername("sa");
            dataSource.setPassword("");

            return dataSource;
        }

        @Bean
        @Primary
        public JavaMailSender javaMailSender() {
            return Mockito.mock(JavaMailSender.class);
        }

        @Bean
        public HashUtil hashUtil() {
            return new HashUtil();
        }
    }