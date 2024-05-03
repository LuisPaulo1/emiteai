package com.emiteai.relatorio.consumer;

import com.emiteai.relatorio.service.PessoaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelatorioConsumer {

    @Autowired
    private PessoaService pessoaService;

    @RabbitListener(queues = "${app-properties.rabbitmq.relatorio.queue}")
    public void receiveMessage(String message) {
        System.out.println("Mensagem recebida: " + message);
        pessoaService.gerarRelatorio();
    }
}
