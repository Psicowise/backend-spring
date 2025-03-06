package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.enums.authenticacao.ERole;
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
        role.setRole(ERole.ADMIN);
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
        String roleName = "USER";
        ERole eRole = ERole.USER;

        // Configurar o mock para retornar Optional.empty() quando findByRole é chamado com o enum
        when(roleRepository.findByRole(eRole)).thenReturn(Optional.empty());

        // Configurar o mock para retornar a role salva
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role savedRole = invocation.getArgument(0);
            savedRole.setId(UUID.randomUUID());
            return savedRole;
        });

        // Act
        ResponseEntity<Role> resultado = roleService.CriarRole(roleName);

        // Assert
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(eRole, resultado.getBody().getRole());
        verify(roleRepository, times(1)).findByRole(eRole);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar role já existente")
    void testCriarRoleJaExistente() {
        // Arrange
        String roleName = "ADMIN";
        ERole eRole = ERole.ADMIN;

        when(roleRepository.findByRole(eRole)).thenReturn(Optional.of(role));

        // Act & Assert
        RoleJaExisteException exception = assertThrows(RoleJaExisteException.class, () -> {
            roleService.CriarRole(roleName);
        });

        assertEquals("Role 'ADMIN' já existe", exception.getMessage());
        verify(roleRepository, times(1)).findByRole(eRole);
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
                createRole(ERole.USER),
                createRole(ERole.PSICOLOGO)
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

    private Role createRole(ERole eRole) {
        Role newRole = new Role();
        newRole.setId(UUID.randomUUID());
        newRole.setRole(eRole);
        return newRole;
    }
}