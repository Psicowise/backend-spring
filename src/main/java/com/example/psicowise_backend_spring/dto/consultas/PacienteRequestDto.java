
package com.example.psicowise_backend_spring.dto.consultas;

import com.example.psicowise_backend_spring.dto.endereco.EnderecoDTO;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequestDto {
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String cpf;
    private EnderecoDTO endereco;
}
