package io.samples.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageProducer {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.openConnection();
        Channel channel = RabbitUtils.createChannel(connection, "inbox");

        String message = "Test message";
        channel.basicPublish(""/*default exchange*/, "inbox"/*routingKey*/, null/*properties*/, message.getBytes("utf-8"));

        channel.close();
        connection.close();
    }
}
