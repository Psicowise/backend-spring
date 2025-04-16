
package com.example.psicowise_backend_spring.dto.endereco;

import lombok.Data;

@Data
public class EnderecoDTO {
    private String rua;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
