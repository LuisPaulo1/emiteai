package com.emiteai.service;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaService {
    Page<PessoaResponseDto> listar(Pageable pageable);
    void solicitarRelatorio();
    String getRelatorio();
    String getStatus();
    void setStatus(String status);
    void buscarRelatorio();
    PessoaResponseDto buscar(Integer id);
    PessoaResponseDto cadastrar(PessoaRequestDto pessoaRequestDto);
    PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto);
    void deletarPorId(Integer id);
}
