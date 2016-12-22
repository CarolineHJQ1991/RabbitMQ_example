package com.caroline.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-22
 * Time: 下午4:51
 */
public class ReceiveLogsTopicForKernal {

    private static final String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明topic转换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //随机创建一个队列,唯一且会自动删除的队列
        String queueName = channel.queueDeclare().getQueue();
        //接收所有与kernal相关的消息
        channel.queueBind(queueName, EXCHANGE_NAME, "kernal.*");

        System.out.println(" [*] Waiting for messages about kernal.To exit press CTRL+C");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            String routingKey = delivery.getEnvelope().getRoutingKey();
            System.out.println(" [x] Received routingKey = " +routingKey + ",   msg = " +message+ ".");
        }
    }

}
