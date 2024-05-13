package com.emiteai.relatorio.consumer;

import com.emiteai.relatorio.service.PessoaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RelatorioConsumer {

    private final PessoaService pessoaService;

    public RelatorioConsumer(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @RabbitListener(queues = "${app-properties.rabbitmq.relatorio.queue}")
    public void receiveMessage(String message) {
        log.info("Mensagem recebida da API Emiteai: {}", message);
        pessoaService.gerarRelatorio();
    }
}
