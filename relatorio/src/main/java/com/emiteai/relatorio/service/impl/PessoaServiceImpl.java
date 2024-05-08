package com.emiteai.relatorio.service.impl;

import com.emiteai.relatorio.model.Pessoa;
import com.emiteai.relatorio.model.Relatorio;
import com.emiteai.relatorio.publisher.RelatorioPublisher;
import com.emiteai.relatorio.repository.PessoaRepository;
import com.emiteai.relatorio.repository.RelatorioPessoaRepository;
import com.emiteai.relatorio.service.PessoaService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private RelatorioPessoaRepository relatorioPessoaRepository;

    @Autowired
    private RelatorioPublisher relatorioPublisher;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorioQueue;

    private List<Pessoa> pessoas = new ArrayList<>();

    @Getter
    private List<Relatorio> relatorioPessoas = new ArrayList<>();

    @Override
    @Transactional
    public void gerarRelatorio() {
        log.info("Gerando relatório...");
        List<Relatorio> relatorioPessoas = relatorioPessoaRepository.findAll();
        if(relatorioPessoas.isEmpty()) {
            log.info("Relatório vazio, gerando novo relatório.");
            gerarNovoRelatorio();
        } else {
            log.info("Relatório já gerado.");
        }
        notificarEmiteai();
    }

    private void gerarNovoRelatorio() {
        relatorioPessoas.clear();
        relatorioPessoaRepository.deleteAll();
        this.pessoas = pessoaRepository.findAll();
        pessoasParaRelatorio();
        relatorioPessoaRepository.saveAll(this.getRelatorioPessoas());
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
        log.info("Enviando mensagem para a fila: {}", relatorioQueue);
        relatorioPublisher.sendMessage(relatorioQueue, "Relatório gerado com sucesso!");
    }
}
