package com.emiteai.service;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.controller.dto.RelatorioPesssoaDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface PessoaService {
    List<PessoaResponseDto> listar();
    void solicitarRelatorio();
    List<RelatorioPesssoaDto> getRelatorio();
    void setSseEmitter(SseEmitter sseEmitter);
    void buscarRelatorio();
    PessoaResponseDto buscar(Integer id);
    PessoaResponseDto cadastrar(PessoaRequestDto pessoaRequestDto);
    PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto);
    void deletarPorId(Integer id);
}
