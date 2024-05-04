package com.emiteai.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatorioDto {
    private Integer id;
    private String nome;
    private String telefone;
    private String cpf;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String estado;
}
