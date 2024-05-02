package com.emiteai.repository;

import com.emiteai.model.Endereco;
import com.emiteai.model.Pessoa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PessoaRepositoryTeste {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Test
    public void findByCpfDeveriaUmaPessoaQuandoOCpfEstiverCadastrado() {
        Endereco endereco = new Endereco();
        endereco.setNumero("123");
        endereco.setComplemento("Casa");
        endereco.setCep("12345-678");
        endereco.setBairro("Centro");
        endereco.setMunicipio("São Paulo");
        endereco.setEstado("SP");
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("João");
        pessoa.setTelefone("11 99999-9999");
        pessoa.setCpf("149.213.350-79");
        pessoa.setEndereco(endereco);
        pessoaRepository.save(pessoa);

        Pessoa pessoaEncontrada = pessoaRepository.findByCpf("149.213.350-79");

        assertEquals(pessoa, pessoaEncontrada);
    }
}
