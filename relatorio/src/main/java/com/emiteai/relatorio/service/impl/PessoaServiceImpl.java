package com.emiteai.relatorio.service.impl;

import com.emiteai.relatorio.model.Pessoa;
import com.emiteai.relatorio.model.Relatorio;
import com.emiteai.relatorio.publisher.RelatorioPublisher;
import com.emiteai.relatorio.repository.PessoaRepository;
import com.emiteai.relatorio.repository.RelatorioPessoaRepository;
import com.emiteai.relatorio.service.PessoaService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${app-properties.rabbitmq.emiteai.queue}")
    private String emiteai;

    @Value("${app-properties.rabbitmq.queue.dlq}")
    private String queueDlq;

    private int retryCount = 0;

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
            try {
                gerarNovoRelatorio();
                notificarEmiteai();
            } catch (Exception e) {
                log.error("Erro ao gerar novo relatório: {}", e.getMessage());
                Message failedMessage = MessageBuilder.withBody(("Erro ao gerar novo relatório: " + e.getMessage()).getBytes())
                        .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                        .setHeader("x-retries", retryCount)
                        .build();
                retryCount++;
                log.info("Enviando mensagem para a fila {}", queueDlq);
                rabbitTemplate.send(queueDlq, failedMessage);
            }
        } else {
            log.info("Relatório já gerado.");
            notificarEmiteai();
        }
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
        log.info("Relatório gerado.");
        log.info("Enviando mensagem para a fila: {}", emiteai);
        relatorioPublisher.sendMessage(emiteai, "Relatório gerado com sucesso!");
    }
}
