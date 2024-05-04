package com.emiteai.util;

import com.emiteai.controller.dto.PessoaRequestDto;

public class PessoaFactory {

    public static PessoaRequestDto criarPessoa() {
       return PessoaRequestDto.builder()
                .nome("Fulano")
                .telefone("11999999999")
                .cpf("844.014.970-07")
                .endereco(EnderecoFactory.criarEndereco())
                .build();
    }
}
