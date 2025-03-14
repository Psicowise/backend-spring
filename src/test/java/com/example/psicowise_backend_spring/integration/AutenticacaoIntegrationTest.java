package com.example.psicowise_backend_spring.integration;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
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
        roleUsuario.setRole(ERole.USER);
        roleRepository.save(roleUsuario);

        // Criar role de admin
        Role roleAdmin = new Role();
        roleAdmin.setRole(ERole.ADMIN);
        roleRepository.save(roleAdmin);
    }

    @Test
    @DisplayName("Deve criar e listar roles com sucesso")
    void testCriarEListarRoles() throws Exception {
        // Verificar as roles iniciais
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder(ERole.USER.name(), ERole.ADMIN.name())));

        // Criar nova role - usando Map para corresponder ao parâmetro esperado
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("role", ERole.PSICOLOGO.name());

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value(ERole.PSICOLOGO.name()));

        // Verificar se a nova role foi adicionada
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].role", containsInAnyOrder(
                        ERole.USER.name(),
                        ERole.ADMIN.name(),
                        ERole.PSICOLOGO.name())));
    }

    @Test
    @DisplayName("Deve falhar ao criar role duplicada")
    void testCriarRoleDuplicada() throws Exception {
        // Tentar criar role que já existe - usando Map
        Map<String, String> roleMap = new HashMap<>();
        roleMap.put("role", ERole.USER.name());

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleMap)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Role '" + ERole.USER.name() + "' já existe")));
    }

    @Test
    @DisplayName("Deve criar e buscar usuário com sucesso")
    void testCriarEBuscarUsuario() throws Exception {
        // Criar um usuário
        CriarUsuarioDto usuarioDto = new CriarUsuarioDto(
                "Maria",
                "Silva",
                "maria.silva@example.com",
                "senha123"
        );

        String usuarioJson = objectMapper.writeValueAsString(usuarioDto);

        // Adicionando role manualmente ao JSON para o endpoint
        String jsonWithRole = usuarioJson.replace("}", ",\"role\":\"" + ERole.USER.name() + "\"}");

        MvcResult result = mockMvc.perform(post("/api/usuarios/criar/comum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithRole))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Maria"))
                .andExpect(jsonPath("$.sobrenome").value("Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@example.com"))
                .andExpect(jsonPath("$.roles[0].role").value(ERole.USER.name())) // Corrigido aqui
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
                "senha123"
        );

        // Adicionar role manualmente
        String jsonWithRole = objectMapper.writeValueAsString(usuarioDto1)
                .replace("}", ",\"role\":\"" + ERole.USER.name() + "\"}");

        mockMvc.perform(post("/api/usuarios/criar/comum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithRole))
                .andExpect(status().isOk());

        // Tentar criar outro usuário com o mesmo email
        CriarUsuarioDto usuarioDto2 = new CriarUsuarioDto(
                "João",
                "Santos",
                "maria.silva@example.com",
                "outrasenha"
        );

        // Adicionar role manualmente
        String jsonWithRole2 = objectMapper.writeValueAsString(usuarioDto2)
                .replace("}", ",\"role\":\"" + ERole.USER.name() + "\"}");

        mockMvc.perform(post("/api/usuarios/criar/comum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithRole2))
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
                "senha123"
        );

        // Adicionar role manualmente
        String jsonWithRole = objectMapper.writeValueAsString(usuarioDto)
                .replace("}", ",\"role\":\"" + ERole.USER.name() + "\"}");

        MvcResult result = mockMvc.perform(post("/api/usuarios/criar/comum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithRole))
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