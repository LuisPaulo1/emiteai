package com.emiteai.controller.dto;

import com.emiteai.model.Endereco;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnderecoDto {

    private Integer id;

    private String numero;

    private String complemento;

    private String cep;

    private String bairro;

    private String municipio;

    private String estado;

    public EnderecoDto(Endereco endereco) {
        this.id = endereco.getId();
        this.numero = endereco.getNumero();
        this.complemento = endereco.getComplemento();
        this.cep = endereco.getCep();
        this.bairro = endereco.getBairro();
        this.municipio = endereco.getMunicipio();
        this.estado = endereco.getEstado();
    }
}
