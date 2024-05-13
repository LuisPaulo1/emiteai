package com.emiteai.relatorio.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RelatorioPublisher {

    final
    RabbitTemplate rabbitTemplate;

    public RelatorioPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String routingKey, String message) {
        rabbitTemplate.convertAndSend(routingKey, message);
    }
}
