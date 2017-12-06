package io.samples.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

public class Main {
    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RabbitConfiguration.DEFAULT_HOST);
        connectionFactory.setPort(RabbitConfiguration.DEFAULT_PORT);
        connectionFactory.setVirtualHost(RabbitConfiguration.DEFAULT_VIRTUAL_HOST);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        RabbitClient client = new RabbitClient(connectionFactory);
        client.start();
    }
}
