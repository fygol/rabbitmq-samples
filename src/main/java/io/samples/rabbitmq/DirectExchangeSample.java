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

/**
 The routing algorithm behind a direct exchange is simple - a message goes to the queues whose binding key exactly matches the routing key of the message.

 14:17:42.354 [pool-1-thread-6] INFO io.samples.rabbitmq.DirectExchangeSample - q1: message-r1
 14:17:42.360 [pool-1-thread-7] INFO io.samples.rabbitmq.DirectExchangeSample - q2: message-r1
 14:17:42.361 [pool-1-thread-7] INFO io.samples.rabbitmq.DirectExchangeSample - q3: message-r1
 14:17:42.361 [pool-1-thread-7] INFO io.samples.rabbitmq.DirectExchangeSample - q2: message-r2
 14:17:42.361 [pool-1-thread-7] INFO io.samples.rabbitmq.DirectExchangeSample - q3: message-r2
 14:17:42.361 [pool-1-thread-7] INFO io.samples.rabbitmq.DirectExchangeSample - q3: message-r3
 */
public class DirectExchangeSample {
    private static final Logger logger = LoggerFactory.getLogger(DirectExchangeSample.class);

    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUri("amqp://guest:guest@localhost:5672");

        // connection
        Connection connection = connectionFactory.newConnection();

        // channel
        Channel producerChannel = connection.createChannel();
        Channel consumerChannel = connection.createChannel();

        // exchange
        final String exchange1 = "x1";
        producerChannel.exchangeDeclare(exchange1, BuiltinExchangeType.DIRECT);

        final String exchange2 = "x2";
        producerChannel.exchangeDeclare(exchange2, BuiltinExchangeType.DIRECT);

        // declare queue
        final String queue1 = "q1";
        consumerChannel.queueDeclare(queue1, false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);
        consumerChannel.queueBind(queue1, exchange1, "r1");
        consumerChannel.queueBind(queue1, exchange2, "r1");
        subscribe(consumerChannel, queue1);

        final String queue2 = "q2";
        consumerChannel.queueDeclare(queue2, false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);
        consumerChannel.queueBind(queue2, exchange1, "r1");
        consumerChannel.queueBind(queue2, exchange1, "r2");
        subscribe(consumerChannel, queue2);

        final String queue3 = "q3";
        consumerChannel.queueDeclare(queue3, false/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);
        consumerChannel.queueBind(queue3, exchange1, "r1");
        consumerChannel.queueBind(queue3, exchange1, "r2");
        consumerChannel.queueBind(queue3, exchange1, "r3");
        subscribe(consumerChannel, queue3);

        // send messages
        producerChannel.basicPublish(exchange1/*exchange*/, "r1"/*routingKey*/, null/*properties*/, "message-r1".getBytes("utf-8"));
        producerChannel.basicPublish(exchange1/*exchange*/, "r2"/*routingKey*/, null/*properties*/, "message-r2".getBytes("utf-8"));
        producerChannel.basicPublish(exchange1/*exchange*/, "r3"/*routingKey*/, null/*properties*/, "message-r3".getBytes("utf-8"));
        producerChannel.basicPublish(exchange2/*exchange*/, "r1"/*routingKey*/, null/*properties*/, "message-r1".getBytes("utf-8"));

        connection.close();
    }

    private static void subscribe(Channel channel, final String queue) throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "utf-8");
                logger.info("{}: {}", queue, message);
            }
        };
        channel.basicConsume(queue, true, consumer);
    }
}
