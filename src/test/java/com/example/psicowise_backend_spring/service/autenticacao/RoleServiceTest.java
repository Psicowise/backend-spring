package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.exception.role.RoleJaExisteException;
import com.example.psicowise_backend_spring.exception.role.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private UUID roleId;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        role = new Role();
        role.setId(roleId);
        role.setRole("admin");
    }

    @Test
    @DisplayName("Deve buscar role por ID com sucesso")
    void testBuscarRolePorIdSucesso() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        ResponseEntity<Role> resultado = roleService.BuscarRolePorId(roleId);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(role, resultado.getBody());
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar role por ID inexistente")
    void testBuscarRolePorIdNaoEncontrada() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(roleRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNaoEncontradaException exception = assertThrows(RoleNaoEncontradaException.class, () -> {
            roleService.BuscarRolePorId(idInexistente);
        });

        assertEquals("Role não encontrada com o identificador: id: " + idInexistente, exception.getMessage());
        verify(roleRepository, times(1)).findById(idInexistente);
    }

    @Test
    @DisplayName("Deve criar role com sucesso")
    void testCriarRoleSucesso() {
        // Arrange
        String nomeRole = "moderador";
        Role novaRole = new Role();
        novaRole.setRole(nomeRole);

        when(roleRepository.findByRole(nomeRole)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(novaRole);

        // Act
        ResponseEntity<Role> resultado = roleService.CriarRole(nomeRole);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(nomeRole, resultado.getBody().getRole());
        verify(roleRepository, times(1)).findByRole(nomeRole);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar role já existente")
    void testCriarRoleJaExistente() {
        // Arrange
        String nomeRole = "admin";
        when(roleRepository.findByRole(nomeRole)).thenReturn(Optional.of(role));

        // Act & Assert
        RoleJaExisteException exception = assertThrows(RoleJaExisteException.class, () -> {
            roleService.CriarRole(nomeRole);
        });

        assertEquals("Role 'admin' já existe", exception.getMessage());
        verify(roleRepository, times(1)).findByRole(nomeRole);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Deve deletar role com sucesso")
    void testDeletarRoleSucesso() {
        // Arrange
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        // Act
        ResponseEntity<String> resultado = roleService.DeletarRole(roleId);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals("Role deletada com sucesso", resultado.getBody());
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar role inexistente")
    void testDeletarRoleInexistente() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();
        when(roleRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RoleNaoEncontradaException exception = assertThrows(RoleNaoEncontradaException.class, () -> {
            roleService.DeletarRole(idInexistente);
        });

        assertEquals("Role não encontrada com o identificador: id: " + idInexistente, exception.getMessage());
        verify(roleRepository, times(1)).findById(idInexistente);
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    @DisplayName("Deve listar todas as roles com sucesso")
    void testListarRolesSucesso() {
        // Arrange
        List<Role> roles = Arrays.asList(
                role,
                createRole("usuario"),
                createRole("moderador")
        );

        when(roleRepository.findAll()).thenReturn(roles);

        // Act
        ResponseEntity<List<Role>> resultado = roleService.ListarRoles();

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(3, resultado.getBody().size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar lista vazia quando não há roles")
    void testListarRolesVazio() {
        // Arrange
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<Role>> resultado = roleService.ListarRoles();

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertTrue(resultado.getBody().isEmpty());
        verify(roleRepository, times(1)).findAll();
    }

    private Role createRole(String roleName) {
        Role newRole = new Role();
        newRole.setId(UUID.randomUUID());
        newRole.setRole(roleName);
        return newRole;
    }
}