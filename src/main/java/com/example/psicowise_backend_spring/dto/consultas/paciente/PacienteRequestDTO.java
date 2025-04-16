
package com.example.psicowise_backend_spring.dto.paciente;

import com.example.psicowise_backend_spring.dto.endereco.EnderecoDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequestDTO {
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String cpf;
    private EnderecoDTO endereco;
}
