package com.emiteai.controller.dto;

import com.emiteai.model.Pessoa;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PessoaResponseDto {

    private Integer id;
    private String nome;
    private String telefone;
    private String cpf;
    private EnderecoDto endereco;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataCriacao;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataAtualizacao;

    public PessoaResponseDto(Pessoa pessoa) {
        this.id = pessoa.getId();
        this.nome = pessoa.getNome();
        this.telefone = pessoa.getTelefone();
        this.cpf = pessoa.getCpf();
        this.endereco = new EnderecoDto(pessoa.getEndereco());
    }
}