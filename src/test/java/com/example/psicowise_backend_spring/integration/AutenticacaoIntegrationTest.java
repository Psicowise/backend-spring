package com.example.psicowise_backend_spring.integration;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AutenticacaoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Role roleUsuario;

    @BeforeEach
    void setUp() {
        // Limpar os dados para cada teste
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();

        // Criar a role padrão
        roleUsuario = new Role();
        roleUsuario.setRole("usuario");
        roleRepository.save(roleUsuario);

        // Criar role de admin
        Role roleAdmin = new Role();
        roleAdmin.setRole("admin");
        roleRepository.save(roleAdmin);
    }

    @Test
    @DisplayName("Deve criar e listar roles com sucesso")
    void testCriarEListarRoles() throws Exception {
        // Verificar as roles iniciais
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder("usuario", "admin")));

        // Criar nova role - usando Map para corresponder ao parâmetro esperado
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("role", "moderador");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("moderador"));

        // Verificar se a nova role foi adicionada
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder("usuario", "admin", "moderador")));
    }

    @Test
    @DisplayName("Deve falhar ao criar role duplicada")
    void testCriarRoleDuplicada() throws Exception {
        // Tentar criar role que já existe - usando Map
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("role", "usuario");

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleMap)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Role 'usuario' já existe")));
    }

    @Test
    @DisplayName("Deve criar e buscar usuário com sucesso")
    void testCriarEBuscarUsuario() throws Exception {
        // Criar um usuário
        CriarUsuarioDto usuarioDto = new CriarUsuarioDto(
                "Maria",
                "Silva",
                "maria.silva@example.com",
                "senha123",
                "usuario"
        );

        String usuarioJson = objectMapper.writeValueAsString(usuarioDto);

        MvcResult result = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.sobrenome").value("Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@example.com"))
                .andExpect(jsonPath("$.role.role").value("usuario"))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Usuario usuarioCriado = objectMapper.readValue(responseContent, Usuario.class);

        // Buscar o usuário pelo ID
        mockMvc.perform(get("/api/usuarios/{id}", usuarioCriado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria.silva@example.com"));

        // Buscar o usuário pelo email
        mockMvc.perform(get("/api/usuarios/email")
                        .param("email", "maria.silva@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.email").value("maria.silva@example.com"));

        // Listar todos os usuários
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Maria"));
    }

    @Test
    @DisplayName("Deve falhar ao criar usuário com email duplicado")
    void testCriarUsuarioEmailDuplicado() throws Exception {
        // Criar o primeiro usuário
        CriarUsuarioDto usuarioDto1 = new CriarUsuarioDto(
                "Maria",
                "Silva",
                "maria.silva@example.com",
                "senha123",
                "usuario"
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto1)))
                .andExpect(status().isOk());

        // Tentar criar outro usuário com o mesmo email
        CriarUsuarioDto usuarioDto2 = new CriarUsuarioDto(
                "João",
                "Santos",
                "maria.silva@example.com",
                "outrasenha",
                "usuario"
        );

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email já cadastrado"));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuario() throws Exception {
        // Criar um usuário
        CriarUsuarioDto usuarioDto = new CriarUsuarioDto(
                "Carlos",
                "Oliveira",
                "carlos.oliveira@example.com",
                "senha123",
                "usuario"
        );

        MvcResult result = mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioDto)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Usuario usuarioCriado = objectMapper.readValue(responseContent, Usuario.class);

        // Atualizar o usuário
        usuarioCriado.setNome("Carlos Atualizado");
        usuarioCriado.setSobrenome("Oliveira Atualizado");

        mockMvc.perform(put("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuarioCriado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"))
                .andExpect(jsonPath("$.sobrenome").value("Oliveira Atualizado"))
                .andExpect(jsonPath("$.email").value("carlos.oliveira@example.com"));

        // Verificar se a atualização foi persistida
        mockMvc.perform(get("/api/usuarios/{id}", usuarioCriado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"))
                .andExpect(jsonPath("$.sobrenome").value("Oliveira Atualizado"));
    }
}