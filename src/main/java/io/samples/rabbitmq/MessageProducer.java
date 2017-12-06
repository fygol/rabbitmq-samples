package io.samples.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // establish connection to message broker
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(RabbitConfiguration.DEFAULT_HOST);
        connectionFactory.setPort(RabbitConfiguration.DEFAULT_PORT);
        connectionFactory.setVirtualHost(RabbitConfiguration.DEFAULT_VIRTUAL_HOST);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        Connection connection = connectionFactory.newConnection();

        // create channel
        Channel channel = connection.createChannel();

        // declare exchange
        // semantics for the declare commands mean “create if not present; otherwise continue
        String exchangeName = "io.samples.logs";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        // declare queue
        channel.queueDeclare("inbox", false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);

        String message = "Test message - " + System.currentTimeMillis();
        channel.basicPublish(exchangeName/*exchange*/, "inbox"/*routingKey*/, null/*properties*/, message.getBytes("utf-8"));

        channel.close();
        connection.close();
    }
}
