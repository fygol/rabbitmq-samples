package io.samples.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    public static void main(String[] args) throws Exception {
        // connection
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // connectionFactory.setUri("amqp://guest:guest@localhost:5672");
        Connection connection = connectionFactory.newConnection(); // creates non-daemon thread

        // channel
        Channel channel = connection.createChannel();

        // declare exchange
        final String exchangeName = "x.inbox";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);

        // declare queue
        final String queueName = "q.inbox";
        channel.queueDeclare(queueName, false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);

        // bind queue to exchange
        channel.queueBind(queueName, exchangeName, "inbox"/*routingKey*/);

        // attach consumer to channel
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                log.info("Received message: " + message);
            }
        };

        log.info("Waiting for incoming messages...");
        channel.basicConsume(queueName, true, consumer);

//        channel.close();
//        connection.close();
    }
}
