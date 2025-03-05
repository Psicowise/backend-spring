package com.example.psicowise_backend_spring.controller.autenticacao;

import com.example.psicowise_backend_spring.entity.autenticacao.Role;
import com.example.psicowise_backend_spring.service.autenticacao.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService rolerService;

    // Buscar role por ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> buscarRolePorId(@PathVariable UUID id) {
        return rolerService.BuscarRolePorId(id);
    }

    // Criar nova role
    @PostMapping
    public ResponseEntity<Role> criarRole(@RequestBody Map<String, String> payload) {
        // espera-se que o JSON tenha algo como { "role": "NOME_DA_ROLE" }
        String role = payload.get("role");
        return rolerService.CriarRole(role);
    }

    // Deletar role
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarRole(@PathVariable UUID id) {
        return rolerService.DeletarRole(id);
    }

    @GetMapping
    public ResponseEntity<List<Role>> listarRoles() {
        return rolerService.ListarRoles();
    }
}

