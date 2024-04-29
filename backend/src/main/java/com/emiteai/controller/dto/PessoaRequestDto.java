package com.emiteai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private EnderecoRequestDto endereco;
}