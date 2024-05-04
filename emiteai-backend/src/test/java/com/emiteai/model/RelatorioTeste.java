package com.emiteai.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RelatorioTeste {

    @Test
    void testarRelatorio() {
        Relatorio relatorio = new Relatorio();
        relatorio.setId(1);
        relatorio.setNome("José");
        relatorio.setCpf("041.388.630-10");
        relatorio.setNumero("11");
        relatorio.setComplemento("Casa");
        relatorio.setCep("12345-678");
        relatorio.setBairro("Centro");
        relatorio.setMunicipio("São Paulo");
        relatorio.setEstado("SP");

        Assertions.assertEquals(1, relatorio.getId());
        Assertions.assertEquals("José", relatorio.getNome());
        Assertions.assertEquals("041.388.630-10", relatorio.getCpf());
        Assertions.assertEquals("11", relatorio.getNumero());
        Assertions.assertEquals("Casa", relatorio.getComplemento());
        Assertions.assertEquals("12345-678", relatorio.getCep());
        Assertions.assertEquals("Centro", relatorio.getBairro());
        Assertions.assertEquals("São Paulo", relatorio.getMunicipio());
        Assertions.assertEquals("SP", relatorio.getEstado());
    }
}
