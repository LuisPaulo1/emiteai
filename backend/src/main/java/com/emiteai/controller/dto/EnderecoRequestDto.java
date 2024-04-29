package com.emiteai.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnderecoRequestDto {

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
