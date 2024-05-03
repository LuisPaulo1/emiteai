package com.emiteai.relatorio.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    @Value("${app-properties.rabbitmq.relatorio.queue}")
    private String relatorioQueue;

    @Bean
    public Queue relatorioQueue() {
        return new Queue(relatorioQueue);
    }
}
