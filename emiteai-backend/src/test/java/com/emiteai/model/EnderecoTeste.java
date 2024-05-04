package com.emiteai.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderecoTeste {

    @Test
    void testarEndereco() {
        Endereco endereco = new Endereco();
        endereco.setId(1);
        endereco.setNumero("123");
        endereco.setComplemento("Casa");
        endereco.setCep("12345678");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");

        Assertions.assertEquals(1, endereco.getId());
        Assertions.assertEquals("123", endereco.getNumero());
        Assertions.assertEquals("Casa", endereco.getComplemento());
        Assertions.assertEquals("12345678", endereco.getCep());
        Assertions.assertEquals("Centro", endereco.getBairro());
        Assertions.assertEquals("São Paulo", endereco.getMunicipio());
        Assertions.assertEquals("SP", endereco.getEstado());
    }
}
