package com.emiteai.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PessoaTeste {

    @Test
    void testarPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1);
        pessoa.setNome("João");
        pessoa.setTelefone("999999999");
        pessoa.setCpf("12345678901");
        pessoa.setEndereco(new Endereco());

        Assertions.assertEquals(1, pessoa.getId());
        Assertions.assertEquals("João", pessoa.getNome());
        Assertions.assertEquals("999999999", pessoa.getTelefone());
        Assertions.assertEquals("12345678901", pessoa.getCpf());
        Assertions.assertNotNull(pessoa.getEndereco());
    }
}
