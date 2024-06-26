package com.emiteai.consumer;

import com.emiteai.service.PessoaService;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class EmiteaiDlqConsumer {

    private final PessoaService pessoaService;

    private final RabbitTemplate rabbitTemplate;

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorio;

    @Value("${app-properties.rabbitmq.queue.dlq}")
    private String queueDlq;

    @Value("${app-properties.rabbitmq.queue.parking-lot}")
    private String queueParkingLot;

    public EmiteaiDlqConsumer(PessoaService pessoaService, RabbitTemplate rabbitTemplate) {
        this.pessoaService = pessoaService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 30000)
    public void receiveDlqMessage() {
        Message message = rabbitTemplate.receive(queueDlq);
        if(message != null) {
            String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("Mensagem recebida da fila {}: {}", queueDlq, messageBody);

            Integer retriesHeader = (Integer) message.getMessageProperties().getHeaders().get("x-retries");
            String novaMensagem = "Gerar relatório novamente";

            if (retriesHeader == null) {
                retriesHeader = 0;
            }

            retriesHeader++;

            if (retriesHeader <= 3) {
                Message updatedMessage = MessageBuilder.withBody(novaMensagem.getBytes(StandardCharsets.UTF_8))
                        .andProperties(message.getMessageProperties())
                        .setHeader("x-retries", retriesHeader)
                        .build();
                log.info("Reenviando mensagem para a fila {}. {} tentativa.", relatorio, retriesHeader);
                rabbitTemplate.send(relatorio, updatedMessage);
            } else {
                log.error("Mensagem não processada após {} tentativas, enviando para fila {}.", --retriesHeader, queueParkingLot);
                this.rabbitTemplate.convertAndSend(queueParkingLot, message);
                this.pessoaService.setStatus("ERRO NA EMISSÃO DO RELATÓRIO");
            }
        }
    }
}