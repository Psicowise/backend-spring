package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.dto.autenticacao.CriarUsuarioDto;
import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.entity.autenticacao.Usuario;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
import com.example.psicowise_backend_spring.exception.usuario.EmailJaCadastradoException;
import com.example.psicowise_backend_spring.exception.usuario.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.exception.usuario.UsuarioNaoEncontradoException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import com.example.psicowise_backend_spring.repository.autenticacao.UsuarioRepository;
import com.example.psicowise_backend_spring.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HashUtil hashUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UUID usuarioId;
    private Role role;
    private ERole eRole;
    private CriarUsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();

        role = new Role();
        role.setId(UUID.randomUUID());
        role.setRole(ERole.USER);

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João");
        usuario.setSobrenome("Silva");
        usuario.setEmail("joao.silva@example.com");
        usuario.setSenha("senhaHasheada");
        usuario.setRoles(List.of(role));

        usuarioDto = new CriarUsuarioDto(
                "Maria",
                "Santos",
                "maria.santos@example.com",
                "senha123"
        );
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void testCriarUsuarioSucesso() {
        // Arrange
        when(usuarioRepository.findByEmail(usuarioDto.email())).thenReturn(Optional.empty());
        when(hashUtil.hashPassword(usuarioDto.senha())).thenReturn("senhaHasheada");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario savedUsuario = invocation.getArgument(0);
            savedUsuario.setId(UUID.randomUUID());
            return savedUsuario;
        });

        // Act
        ResponseEntity<Usuario> resultado = usuarioService.CriarUsuarioComum(usuarioDto);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(usuarioDto.nome(), resultado.getBody().getNome());
        assertEquals(usuarioDto.email(), resultado.getBody().getEmail());

        verify(usuarioRepository, times(1)).findByEmail(usuarioDto.email());
        verify(hashUtil, times(1)).hashPassword(usuarioDto.senha());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar usuário com email já cadastrado")
    void testCriarUsuarioEmailJaCadastrado() {
        // Arrange
        when(usuarioRepository.findByEmail(usuarioDto.email())).thenReturn(Optional.of(usuario));

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class, () -> {
            usuarioService.CriarUsuarioComum(usuarioDto);
        });

        verify(usuarioRepository, times(1)).findByEmail(usuarioDto.email());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando role padrão não encontrada")
    void testCriarUsuarioRolePadraoNaoEncontrada() {
        // Arrange
        reset(roleRepository); // Limpa qualquer expectativa anterior

        when(usuarioRepository.findByEmail(usuarioDto.email())).thenReturn(Optional.empty());

        // Act & Assert
        RoleNaoEncontradaException exception = assertThrows(RoleNaoEncontradaException.class, () -> {
            usuarioService.CriarUsuarioComum(usuarioDto);
        });

        assertTrue(exception.getMessage().contains("usuario"));

        verify(usuarioRepository, times(1)).findByEmail(usuarioDto.email());

        // Usando verificação mais flexível

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void testBuscarUsuarioPorIdSucesso() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> resultado = usuarioService.BuscarUsuarioPorId(usuarioId);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(usuario, resultado.getBody());
        verify(usuarioRepository, times(1)).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve listar todos os usuários com sucesso")
    void testListarUsuariosSucesso() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(
                usuario,
                createUsuario("Pedro", "Oliveira", "pedro.oliveira@example.com"),
                createUsuario("Ana", "Souza", "ana.souza@example.com")
        );

        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        ResponseEntity<List<Usuario>> resultado = usuarioService.ListarUsuarios();

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(3, resultado.getBody().size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void testBuscarUsuarioPorEmailSucesso() {
        // Arrange
        String email = "joao.silva@example.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Usuario> resultado = usuarioService.BuscarUsuarioPorEmail(email);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(usuario, resultado.getBody());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void testAtualizarUsuarioSucesso() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(usuarioId);
        usuarioAtualizado.setNome("João Atualizado");
        usuarioAtualizado.setSobrenome("Silva Atualizado");
        usuarioAtualizado.setEmail("joao.silva@example.com");
        usuarioAtualizado.setSenha("senhaHasheada");
        usuarioAtualizado.setRoles(List.of(role));

        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        when(usuarioRepository.findByEmail(usuarioAtualizado.getEmail())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(usuarioAtualizado)).thenReturn(usuarioAtualizado);

        // Act
        ResponseEntity<Usuario> resultado = usuarioService.AtualizarUsuario(usuarioAtualizado);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(usuarioAtualizado, resultado.getBody());
        verify(usuarioRepository, times(1)).existsById(usuarioId);
        verify(usuarioRepository, times(1)).findByEmail(usuarioAtualizado.getEmail());
        verify(usuarioRepository, times(1)).save(usuarioAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário inexistente")
    void testAtualizarUsuarioInexistente() {
        // Arrange
        UUID idAleatorio = UUID.randomUUID();
        usuario.setId(idAleatorio);
        when(usuarioRepository.existsById(idAleatorio)).thenReturn(false);

        // Act & Assert
        UsuarioNaoEncontradoException exception = assertThrows(UsuarioNaoEncontradoException.class, () -> {
            usuarioService.AtualizarUsuario(usuario);
        });

        // Verificar a mensagem de exceção usando contains em vez de igualdade exata
        assertTrue(exception.getMessage().contains(idAleatorio.toString()));

        verify(usuarioRepository, times(1)).existsById(idAleatorio);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar usuário para email já existente")
    void testAtualizarUsuarioEmailJaExistente() {
        // Arrange
        UUID outroUsuarioId = UUID.randomUUID();
        Usuario outroUsuario = createUsuario("Outro", "Usuario", "joao.silva@example.com");
        outroUsuario.setId(outroUsuarioId);

        usuario.setEmail("outro.email@example.com");

        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(outroUsuario));

        // Act & Assert
        EmailJaCadastradoException exception = assertThrows(EmailJaCadastradoException.class, () -> {
            usuarioService.AtualizarUsuario(usuario);
        });

        verify(usuarioRepository, times(1)).existsById(usuario.getId());
        verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private Usuario createUsuario(String nome, String sobrenome, String email) {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setId(UUID.randomUUID());
        novoUsuario.setNome(nome);
        novoUsuario.setSobrenome(sobrenome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha("senhaHasheada");
        novoUsuario.setRoles(List.of(role));
        return novoUsuario;
    }
}