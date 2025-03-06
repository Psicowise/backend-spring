package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.exception.role.RoleJaExisteException;
import com.example.psicowise_backend_spring.exception.role.RoleNaoEncontradaException;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<Role> BuscarRolePorId(UUID id) {
        try {
            return ResponseEntity.ok(roleRepository.findById(id)
                    .orElseThrow(() -> new RoleNaoEncontradaException("id: " + id)));
        } catch (RoleNaoEncontradaException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao buscar role por ID: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Role> CriarRole(String role) {
        try {
            // Verificar se a role já existe
            if (roleRepository.findByRole(role).isPresent()) {
                throw new RoleJaExisteException(role);
            }

            var roleEntity = new Role();
            roleEntity.setRole(role);
            roleRepository.save(roleEntity);

            return ResponseEntity.ok(roleEntity);
        } catch (RoleJaExisteException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao criar role: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<String> DeletarRole(UUID id) {
        try {
            // Verificar se a role existe
            Role role = roleRepository.findById(id)
                    .orElseThrow(() -> new RoleNaoEncontradaException("id: " + id));

            roleRepository.delete(role);

            return ResponseEntity.ok("Role deletada com sucesso");
        } catch (RoleNaoEncontradaException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Erro ao deletar role: " + e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<List<Role>> ListarRoles() {
        try {
            return ResponseEntity.ok(roleRepository.findAll());
        } catch (Exception e) {
            System.err.println("Erro ao listar roles: " + e.getMessage());
            throw e;
        }
    }
}