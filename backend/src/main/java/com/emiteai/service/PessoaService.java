package com.emiteai.service;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;

import java.util.List;

public interface PessoaService {
    List<PessoaResponseDto> listar();
    PessoaResponseDto buscar(Integer id);
    PessoaResponseDto cadastrar(PessoaRequestDto pessoaRequestDto);
    PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto);
    void deletarPorId(Integer id);
}
