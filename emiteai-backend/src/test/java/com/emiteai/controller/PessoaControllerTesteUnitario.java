package com.emiteai.controller;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import com.emiteai.util.PessoaFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PessoaController.class)
public class PessoaControllerTesteUnitario {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PessoaService pessoaService;

    private Integer idExiste;
    private Integer idNaoExiste;
    private PessoaRequestDto pessoaRequestDto;

    @BeforeEach
    void setUp() throws Exception {
        idNaoExiste = Integer.MAX_VALUE;
        idExiste = 1;
        pessoaRequestDto = PessoaFactory.criarPessoa();
        Page<PessoaResponseDto> page = new PageImpl<>(new ArrayList<>());

        when(pessoaService.listar(any())).thenReturn(page);

        when(pessoaService.getRelatorio()).thenReturn("");

        when(pessoaService.buscar(idExiste)).thenReturn(new PessoaResponseDto());
        when(pessoaService.buscar(idNaoExiste)).thenThrow(new RecursoNaoEncontradoException());

        when(pessoaService.cadastrar(any())).thenReturn(new PessoaResponseDto());

        when(pessoaService.atualizar(eq(idExiste), any(PessoaRequestDto.class))).thenReturn(new PessoaResponseDto());
        when(pessoaService.atualizar(eq(idNaoExiste), any(PessoaRequestDto.class))).thenThrow(new RecursoNaoEncontradoException());

        doNothing().when(pessoaService).deletarPorId(idExiste);
        doThrow(new RecursoNaoEncontradoException()).when(pessoaService).deletarPorId(idNaoExiste);
    }

    @Test
    void findAllDeveriaRetornarOsRecursosComStatusOk() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/v1/pessoas")
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpectAll(status().isOk());
    }

    @Test
    void getRelatorioDeveriaRetornarArquivoCsv() throws Exception {
        ResultActions result =
                mockMvc.perform(get("/v1/pessoas/relatorio")
                        .contentType(MediaType.APPLICATION_JSON));
        result.andExpectAll(status().isOk());
    }

    @Test
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
