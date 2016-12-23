package com.caroline.rpc;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import com.rabbitmq.client.AMQP.BasicProperties;
/**
 * User: Caroline.Han
 * Date: 2016-12-23
 * Time: 下午5:44
 */
public class RPCClient {
    private static final String requestQueueName = "rpc_queue";
    private static Channel channel;
    private static QueueingConsumer consumer;
    private static Connection connection;
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        String replyQueueName = channel.queueDeclare().getQueue();
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(replyQueueName, true, consumer);
        connection.close();
    }

    public String call(String message) throws IOException, InterruptedException {
        String response = null;
        String corrId = UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties
                                    .Builder()
                                    .correlationId(corrId)
                                    .replyTo(requestQueueName)
                                    .build();
        channel.basicPublish("",requestQueueName, props, message.getBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody());
                break;
            }
        }
        return response;
    }

}
