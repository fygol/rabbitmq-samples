package io.samples.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RabbitClient implements ShutdownListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ConnectionFactory connectionFactory;

    private Connection connection;

    public RabbitClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void start() {
        try {
            connection = connectionFactory.newConnection();
            logger.info("Connected to {}:{}", connectionFactory.getHost(), connectionFactory.getPort());
        } catch (Exception e) {
            logger.error("Failed to connect to {}:{}", connectionFactory.getHost(), connectionFactory.getPort(), e);
        }
    }

    public void shutdownCompleted(ShutdownSignalException cause) {
        logger.info("Close connection on shutdown event");
        try {
            connection.close();
        } catch (IOException e) {
            logger.error("Failed to close connection", e);
        }
    }
}
