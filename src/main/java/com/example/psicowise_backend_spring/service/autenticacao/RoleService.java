package com.example.psicowise_backend_spring.service.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.repository.autenticacao.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public ResponseEntity<Role> BuscarRolePorId(UUID id){

        var role = roleRepository.findById(id).get();

        if (role == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(role);
    }

    public ResponseEntity<Role> CriarRole(String role){

        var roleOpt = roleRepository.findByRole(role);
        if (roleOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        var roleEntity = new Role();
        roleEntity.setRole(role);
        roleRepository.save(roleEntity);

        return ResponseEntity.ok(roleEntity);
    }

    public ResponseEntity<String> DeletarRole(UUID id){

        var role = roleRepository.findById(id).get();
        if (role == null) {
            return ResponseEntity.notFound().build();
        }

        roleRepository.delete(role);

        return ResponseEntity.ok("Role deletada com sucesso");
    }

    public ResponseEntity<List<Role>> ListarRoles() { return ResponseEntity.ok(roleRepository.findAll()); } // <List<Role>>
}
