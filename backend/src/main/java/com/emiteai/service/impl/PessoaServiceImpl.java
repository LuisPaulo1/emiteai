package com.emiteai.service.impl;

import com.emiteai.controller.dto.PessoaRequestDto;
import com.emiteai.controller.dto.PessoaResponseDto;
import com.emiteai.model.Endereco;
import com.emiteai.model.Pessoa;
import com.emiteai.repository.PessoaRepository;
import com.emiteai.service.PessoaService;
import com.emiteai.service.exception.RecursoNaoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Override
    public List<PessoaResponseDto> listar() {
        List<Pessoa> pessoas = pessoaRepository.findAll();
        return pessoas.stream().map(PessoaResponseDto::new).toList();
    }

    @Override
    public PessoaResponseDto buscar(Integer id) {
        Pessoa pessoa = buscarPessoaPorId(id);
        return new PessoaResponseDto(pessoa);
    }

    @Override
    @Transactional
    public PessoaResponseDto salvar(PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = new Pessoa();
        converterParaModel(pessoa, pessoaRequestDto);
        pessoa = pessoaRepository.save(pessoa);
        return new PessoaResponseDto(pessoa);
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(Integer id, PessoaRequestDto pessoaRequestDto) {
        Pessoa pessoa = buscarPessoaPorId(id);
        converterParaModel(pessoa, pessoaRequestDto);
        pessoa = pessoaRepository.save(pessoa);
        return new PessoaResponseDto(pessoa);
    }

    @Override
    @Transactional
    public void deletar(Integer id) {
        try {
            pessoaRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e) {
            throw new RecursoNaoEncontradoException();
        }
    }

    private Pessoa buscarPessoaPorId(Integer id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(String.format("Pessoa com id %d não encontrada.", id)));
    }

    private void converterParaModel(Pessoa pessoa, PessoaRequestDto pessoaRequestDto) {
        pessoa.setNome(pessoaRequestDto.getNome());
        pessoa.setTelefone(pessoaRequestDto.getTelefone());
        pessoa.setCpf(pessoaRequestDto.getCpf());
        Endereco endereco = new Endereco();
        endereco.setNumero(pessoaRequestDto.getNumero());
        endereco.setComplemento(pessoaRequestDto.getComplemento());
        endereco.setCep(pessoaRequestDto.getCep());
        endereco.setBairro(pessoaRequestDto.getBairro());
        endereco.setMunicipio(pessoaRequestDto.getMunicipio());
        endereco.setEstado(pessoaRequestDto.getEstado());
        pessoa.setEndereco(endereco);
    }
}
