package com.emiteai.service;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.model.Pessoa;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import com.emiteai.service.impl.PessoaServiceImpl;
import com.emiteai.util.PessoaFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class PessoaServiceTesteUnitario {

    @InjectMocks
    private PessoaServiceImpl pessoaService;

    @Mock
    private PessoaRepository pessoaRepository;

    @Spy
    private ModelMapper modelMapper;

    private Integer idExiste;
    private Integer idNaoExiste;
    private PessoaRequestDto pessoaRequestDto;
    private Pageable page = Pageable.unpaged();

    @BeforeEach
    void setUp() throws Exception {
        idNaoExiste = Integer.MAX_VALUE;
        idExiste = 1;
        pessoaRequestDto = PessoaFactory.criarPessoa();

        when(pessoaRepository.findAll(page)).thenReturn(new PageImpl<>(List.of(new Pessoa())));

        when(pessoaRepository.findById(idExiste)).thenReturn(Optional.of(new Pessoa()));
        when(pessoaRepository.findById(idNaoExiste)).thenReturn(Optional.empty());

        when(pessoaRepository.save(any())).thenReturn(new Pessoa());

        doNothing().when(pessoaRepository).deleteById(idExiste);
        doThrow(EmptyResultDataAccessException.class).when(pessoaRepository).deleteById(idNaoExiste);
    }

    @Test
    void listarDeveriaRetornarListaDePessoas() {
        Page<PessoaResponseDto> pessoas = pessoaService.listar(page);
        Assertions.assertFalse(pessoas.isEmpty());
    }

    @Test
    void buscarDeveriaRetornarPessoaQuandoIdExiste() {
        PessoaResponseDto pessoa = pessoaService.buscar(idExiste);
        Assertions.assertNotNull(pessoa);
    }

    @Test
    void buscarDeveriaLancarExcecaoQuandoIdNaoExiste() {
        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.buscar(idNaoExiste));
    }

    @Test
    void cadastrarDeveriaRetornarPessoaSalva() {
        PessoaResponseDto pessoa = pessoaService.cadastrar(pessoaRequestDto);
        Assertions.assertNotNull(pessoa);
    }

    @Test
    void atualizarDeveriaRetornarPessoaAtualizada() {
        PessoaResponseDto pessoa = pessoaService.atualizar(idExiste, pessoaRequestDto);
        Assertions.assertNotNull(pessoa);
    }

    @Test
    void atualizarDeveriaLancarExcecaoQuandoIdNaoExiste() {
        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.atualizar(idNaoExiste, pessoaRequestDto));
    }

    @Test
    void deletarPorIdDeveriaRemoverPessoa() {
        pessoaService.deletarPorId(idExiste);
        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.buscar(idNaoExiste));
    }

    @Test
    void deletarPorIdDeveriaLancarExcecaoQuandoIdNaoExiste() {
        Assertions.assertThrows(RecursoNaoEncontradoException.class, () -> pessoaService.deletarPorId(idNaoExiste));
    }
}
