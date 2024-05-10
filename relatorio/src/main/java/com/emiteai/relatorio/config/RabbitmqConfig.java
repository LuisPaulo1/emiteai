package com.emiteai.relatorio.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorio;

    @Value("${app-properties.rabbitmq.emiteai.queue}")
    private String emiteai;

    @Value("${app-properties.rabbitmq.queue.dlq}")
    private String queueDlq;

    @Value("${app-properties.rabbitmq.queue.dlx}")
    private String exchangeDlx;

    @Value("${app-properties.rabbitmq.queue.parking-lot}")
    private String queueParkingLot;

    @Bean
    public Queue relatorio() {
        return QueueBuilder.durable(relatorio)
                .withArgument("x-dead-letter-exchange", exchangeDlx)
                .withArgument("x-dead-letter-routing-key", queueDlq)
                .build();
    }

    @Bean
    public Queue emiteai() {
        return QueueBuilder.durable(emiteai)
                .withArgument("x-dead-letter-exchange", exchangeDlx)
                .withArgument("x-dead-letter-routing-key", queueDlq)
                .build();
    }

    @Bean
    public DirectExchange exchangeDlx() {
        return new DirectExchange(exchangeDlx);
    }

    @Bean
    public Queue queueDlq() {
        return QueueBuilder.durable(queueDlq).build();
    }

    @Bean
    public Queue queueParkingLot() {
        return QueueBuilder.durable(queueParkingLot).build();
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(queueDlq()).to(exchangeDlx()).with(queueDlq);
    }
}