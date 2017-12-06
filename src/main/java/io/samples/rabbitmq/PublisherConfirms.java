package io.samples.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublisherConfirms {
    private static final Logger logger = LoggerFactory.getLogger(PublisherConfirms.class);

    final static String QUEUE_NAME = "confirm-test";

    public static void main(String[] args) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        new Thread(new Consumer(connectionFactory)).start();
        new Thread(new Publisher(connectionFactory)).start();
    }

    static class Publisher implements Runnable {
        private ConnectionFactory connectionFactory;

        public Publisher(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public void run() {
            try {
                logger.debug("Start publish");
                Connection conn = connectionFactory.newConnection();
                Channel channel = conn.createChannel();
                channel.queueDeclare(QUEUE_NAME, true/*durable*/, false/*exclusive*/, false/*autoDelete*/, null/*arguments*/);
                channel.confirmSelect(); // Enables publisher acknowledgements on channel

                // Publish
                for (long i = 0; i < 10; ++i) {
                    channel.basicPublish(""/*exchange*/, QUEUE_NAME/*routing key*/, MessageProperties.PERSISTENT_BASIC, "nop".getBytes());
                }

                /*
                * Wait until all messages published since the last call have been either ack'd or nack'd by the broker.
                * If any of the messages were nack'd, waitForConfirmsOrDie will throw an IOException.
                * When called on a non-Confirm channel, it will throw an IllegalStateException.
                */
                channel.waitForConfirmsOrDie();

                // Cleanup
                channel.queueDelete(QUEUE_NAME);
                channel.close();
                conn.close();
                logger.debug("end publish");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer implements Runnable {
        private ConnectionFactory connectionFactory;

        public Consumer(ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        public void run() {
            try {
                logger.debug("Start consume");
                // Setup
                Connection conn = connectionFactory.newConnection();
                Channel ch = conn.createChannel();
                ch.queueDeclare(QUEUE_NAME, true, false, false, null);

                // Consume
                QueueingConsumer qc = new QueueingConsumer(ch);
                ch.basicConsume(QUEUE_NAME, true, qc);
                for (int i = 0; i < 10; ++i) {
                    logger.debug("next delivery");
                    qc.nextDelivery();
                }

                // Cleanup
                ch.close();
                conn.close();

                logger.debug("End consume");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
