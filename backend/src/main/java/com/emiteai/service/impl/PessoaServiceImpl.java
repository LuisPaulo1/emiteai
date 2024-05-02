package com.emiteai.service.impl;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.model.Pessoa;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    public List<PessoaResponseDto> listar() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return modelMapper.map(pessoas, List.class);
    }

    @Override
    public PessoaResponseDto buscar(Integer id) {
        Pessoa pessoa = buscarPessoaPorId(id);
        return modelMapper.map(pessoa, PessoaResponseDto.class);
    }

    @Override
    @Transactional
    public PessoaResponseDto salvar(PessoaRequestDto pessoaRequestDto) {
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
            throw new RecursoNaoEncontradoException();
        }
        pessoaRepository.delete(pessoa);
    }

    private Pessoa buscarPessoaPorId(Integer id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id)));
    }
}
