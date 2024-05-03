package com.emiteai.service.impl;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.controller.dto.RelatorioDto;
import com.emiteai.model.Pessoa;
import com.emiteai.publisher.EmiteaiPublisher;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.repository.RelatorioRepository;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmiteaiPublisher emiteaiPublisher;

    private SseEmitter sseEmitter;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorioQueue;

    private String relatorioStatus = "EM_PROCESSAMENTO";

    private List<RelatorioDto> relatorioPessoas = new ArrayList<>();

    @Override
    public List<PessoaResponseDto> listar() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return modelMapper.map(pessoas, List.class);
    }

    @Override
    public void solicitarRelatorio() {
        emiteaiPublisher.sendMessage(relatorioQueue, "Solicitando emissão de relatório");
    }

    @Override
    public List<RelatorioDto> getRelatorio() {
        List<RelatorioDto> relatorio = new ArrayList<>(this.relatorioPessoas);
        deletarRelatorio();
        return relatorio;
    }

    @Override
    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void buscarRelatorio() {
        List<RelatorioDto> relatorioPessoasDto = modelMapper.map(relatorioRepository.findAll(), List.class);
        this.relatorioPessoas = relatorioPessoasDto;
        this.relatorioStatus = "CONCLUIDO";
        if (this.sseEmitter != null) {
            try {
                this.sseEmitter.send(this.relatorioStatus);
                this.sseEmitter.complete();
            } catch (IOException e) {
                this.sseEmitter.completeWithError(e);
            }
        }
    }

    @Override
    public PessoaResponseDto buscar(Integer id) {
        Pessoa pessoa = buscarPessoaPorId(id);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto cadastrar(PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = new Pessoa();
        modelMapper.map(pessoaRequestDto, pessoa);
        pessoa = pessoaRepository.save(pessoa);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = buscarPessoaPorId(id);
        modelMapper.map(pessoaRequestDto, pessoa);
        pessoa = pessoaRepository.save(pessoa);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public void deletarPorId(Integer id) {
        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);
        if (pessoa == null) {
            throw new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id));
        }
        pessoaRepository.delete(pessoa);
    }

    private Pessoa buscarPessoaPorId(Integer id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id)));
    }

    private void deletarRelatorio() {
        relatorioRepository.deleteAll();
    }
}
