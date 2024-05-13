package com.emiteai.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.util.PessoaFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PessoaControllerTesteIntegracao {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void findAllDeveriaRetornarOsRecursosComStatusOk() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/v1/pessoas")
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpectAll(status().isOk());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void findByIdDeveriaRetornarORecursoComStatusOk() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/v1/pessoas/{id}", idExiste)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpectAll(status().isOk());
    }

    @Test
    void findByIdDeveriaRetornarNotFoundQuandoRecursoNaoExiste() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/v1/pessoas/{id}", idNaoExiste)
                        .contentType(MediaType.APPLICATION_JSON));

        result.andExpectAll(status().isNotFound());
    }

    @Test
    void saveDeveriaRetornarORecursoCriadoComStatusCreated() throws Exception {
        ResultActions result =
                mockMvc.perform(post("/v1/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaRequestDto)));

        result.andExpectAll(status().isCreated());
    }

    @Test
    void saveDeveriaRetornarBadRequestQuandoRequisicaoInvalida() throws Exception {
        pessoaRequestDto.setNome(null);
        ResultActions result =
                mockMvc.perform(post("/v1/pessoas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaRequestDto)));

        result.andExpectAll(status().isBadRequest());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)",
    })
    void updateDeveriaRetornarORecursoAtualizadoComStatusOk() throws Exception {
        pessoaRequestDto.setNome("Ciclano");
        ResultActions result =
                mockMvc.perform(put("/v1/pessoas/{id}", idExiste)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaRequestDto)));
        result.andExpectAll(status().isOk());
    }

    @Test
    void updateDeveriaRetornarNotFoundQuandoRecursoNaoExiste() throws Exception {
        ResultActions result =
                mockMvc.perform(put("/v1/pessoas/{id}", idNaoExiste)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pessoaRequestDto)));
        result.andExpectAll(status().isNotFound());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO endereco (id, numero, complemento, cep,  bairro, municipio, estado) VALUES (1, '123', 'apto 123', '12345678', 'Centro', 'São Paulo', 'SP')",
            "INSERT INTO pessoa (id, nome, telefone, cpf, endereco_id) VALUES (1, 'Fulano', '11999999999', '844.014.970-07', 1)"
    })
    void deleteDeveriaRetornarNoContentComStatusNoContent() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/v1/pessoas/{id}", idExiste)
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpectAll(status().isNoContent());
    }

    @Test
    void deleteDeveriaRetornarNotFoundQuandoRecursoNaoExiste() throws Exception {
        ResultActions result =
                mockMvc.perform(delete("/v1/pessoas/{id}", idNaoExiste)
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpectAll(status().isNotFound());
    }
}
