package com.caroline.routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-22
 * Time: 下午3:43
 */
public class ReceiveLogsDirect {

    //定义转发器的名称
    private static final String EXCHANGE_NAME = "ex_log_direct";
    private static final String[] SEVERITIES = { "info", "warning", "error" };
    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明direct转换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();
        //String severity = getSeverity();
        //指定binding_key
        String severity = "warning";
        channel.queueBind(queueName, EXCHANGE_NAME, severity );
        System.out.println(" [*] Waiting for " + severity + " logs. To exit press CTRL+C");
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(queueName, true, consumer);

        while (true){
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(" [x] Recevied '" + message + "'");
        }
    }

    private static String getSeverity() {
        Random random = new Random();
        int ranVal = random.nextInt(3);
        return SEVERITIES[ranVal];
    }
}
