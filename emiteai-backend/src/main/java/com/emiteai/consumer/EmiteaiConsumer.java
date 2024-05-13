package com.emiteai.consumer;

import com.emiteai.service.PessoaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class EmiteaiConsumer {

    private final PessoaService pessoaService;

    public EmiteaiConsumer(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @RabbitListener(queues = "${app-properties.rabbitmq.emiteai.queue}")
    public void receiveMessage(String message) {
        log.info("Mensagem recebida da API relatório: {}", message);
        pessoaService.buscarRelatorio();
    }
}
