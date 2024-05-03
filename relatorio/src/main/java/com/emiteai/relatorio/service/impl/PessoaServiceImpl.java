package com.emiteai.relatorio.service.impl;

import com.emiteai.relatorio.model.Pessoa;
import com.emiteai.relatorio.model.Relatorio;
import com.emiteai.relatorio.publisher.RelatorioPublisher;
import com.emiteai.relatorio.repository.PessoaRepository;
import com.emiteai.relatorio.repository.RelatorioRepository;
import com.emiteai.relatorio.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RelatorioRepository relatorioRepository;

    @Autowired
    private RelatorioPublisher relatorioPublisher;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorioQueue;

    private List<Pessoa> pessoas = new ArrayList<>();

    private List<Relatorio> relatorioPessoas = new ArrayList<>();

    @Override
    @Transactional
    public void gerarRelatorio() {
        this.pessoas = pessoaRepository.findAll();
        pessoasParaRelatorio();
        relatorioRepository.saveAll(relatorioPessoas);
        notificarEmiteai();
    }

    private void pessoasParaRelatorio() {
        this.pessoas.forEach(pessoa -> {
            Relatorio relatorio = new Relatorio();
            relatorio.setNome(pessoa.getNome());
            relatorio.setTelefone(pessoa.getTelefone());
            relatorio.setCpf(pessoa.getCpf());
            relatorio.setNumero(pessoa.getEndereco().getNumero());
            relatorio.setComplemento(pessoa.getEndereco().getComplemento());
            relatorio.setCep(pessoa.getEndereco().getCep());
            relatorio.setBairro(pessoa.getEndereco().getBairro());
            relatorio.setMunicipio(pessoa.getEndereco().getMunicipio());
            relatorio.setEstado(pessoa.getEndereco().getEstado());
            relatorioPessoas.add(relatorio);
        });
    }

    private void notificarEmiteai() {
        relatorioPublisher.sendMessage(relatorioQueue, "Relatório gerado com sucesso!");
    }
}
