package com.emiteai.util;

import com.emiteai.controller.dto.EnderecoRequestDto;

public class EnderecoFactory {
    public static EnderecoRequestDto criarEndereco() {
        return EnderecoRequestDto.builder()
                .numero("123")
                .complemento("Casa")
                .cep("12345678")
                .bairro("Centro")
                .municipio("São Paulo")
                .estado("SP")
                .build();
    }
}
