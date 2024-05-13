package com.emiteai.service;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.service.exception.NegocioException;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import com.emiteai.util.PessoaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class PessoaServiceTesteIntegracao {

    @Autowired
    private PessoaService pessoaService;

    private Integer idExiste;
    private Integer idNaoExiste;
    private PessoaRequestDto pessoaRequestDto;

    @BeforeEach
    void setUp() throws Exception {
        idNaoExiste = Integer.MAX_VALUE;
        idExiste = 1;
        pessoaRequestDto = PessoaFactory.criarPessoa();
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void listarDeveriaRetornarListaDePessoas() {
      Pageable page = Pageable.unpaged();
      Page<PessoaResponseDto> pessoas = pessoaService.listar(page);
      Assertions.assertFalse(pessoas.isEmpty());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void buscarDeveriaRetornarPessoaQuandoIdExiste() {
      PessoaResponseDto pessoa = pessoaService.buscar(idExiste);
      Assertions.assertNotNull(pessoa);
    }

    @Test
    void buscarDeveriaLancarExcecaoQuandoIdNaoExiste() {
      Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.buscar(idNaoExiste));
    }

    @Test
    void cadastrarDeveriaRetornarPessoaSalvaQuandoCpfNaoEstaCadastrado() {
      PessoaResponseDto pessoa = pessoaService.cadastrar(pessoaRequestDto);
      Assertions.assertNotNull(pessoa);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void cadastrarDeveriaLancarNegocioExceptionQuandoCpfJaEstaCadastrado() {
      pessoaRequestDto.setCpf("844.014.970-07");
      Assertions.assertThrows(NegocioException.class, () -> pessoaService.cadastrar(pessoaRequestDto));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void atualizarDeveriaRetornarPessoaAtualizada() {
      pessoaRequestDto.setNome("Ciclano");
      PessoaResponseDto pessoa = pessoaService.atualizar(idExiste, pessoaRequestDto);
      Assertions.assertNotNull(pessoa);
    }

    @Test
    void atualizarDeveriaLancarExcecaoQuandoIdNaoExiste() {
      Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.atualizar(idNaoExiste, pessoaRequestDto));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)",
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (2, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (2, 'Ciclano', '11999999999', '424.012.830-72', 2)"

    })
    void atualizarDeveriaLancarNegocioExceptionQuandoCpfJaEstaCadastrado() {
      pessoaRequestDto.setCpf("424.012.830-72");
      Assertions.assertThrows(NegocioException.class, () -> pessoaService.atualizar(idExiste, pessoaRequestDto));
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void deletarPorIdDeveriaRemoverPessoa() {
      pessoaService.deletarPorId(idExiste);
      Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.buscar(idExiste));
    }

    @Test
    void deletarPorIdDeveriaLancarExcecaoQuandoIdNaoExiste() {
      Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.deletarPorId(idNaoExiste));
    }
}
