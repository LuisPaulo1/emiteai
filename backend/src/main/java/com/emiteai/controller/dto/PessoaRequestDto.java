package com.emiteai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
public class PessoaRequestDto {

    @NotBlank
    private String nome;

    @NotBlank
    private String telefone;

    @CPF
    @NotBlank
    private String cpf;

    @NotBlank
    private String numero;

    private String complemento;

    @NotBlank
    private String cep;

    @NotBlank
    private String bairro;

    @NotBlank
    private String municipio;

    @NotBlank
    private String estado;
}