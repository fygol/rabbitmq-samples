package io.samples.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public final class RabbitUtils {
    public static Connection openConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672); // default port
        return connectionFactory.newConnection();
    }

    public static Channel createChannel(Connection connection, String queueName) throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);

        return channel;
    }
}
