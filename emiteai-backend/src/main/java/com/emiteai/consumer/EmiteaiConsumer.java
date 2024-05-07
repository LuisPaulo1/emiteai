package com.emiteai.consumer;

import com.emiteai.service.PessoaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class EmiteaiConsumer {

    @Autowired
    private PessoaService pessoaService;

    @RabbitListener(queues = "${app-properties.rabbitmq.relatorio.queue}")
    public void receiveMessage(String message) {
        log.info("Mensagem recebida da API relatório: {}", message);
        pessoaService.buscarRelatorio();
    }
}
