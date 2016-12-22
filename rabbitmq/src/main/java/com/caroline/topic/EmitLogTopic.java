package com.caroline.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-22
 * Time: 下午4:41
 */
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //声明topic类型的转换器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String[] routing_keys = new String[] { "kernal.info", "cron.warning",
                                "auth.info", "kernal.critical"};
        for (String routing_key : routing_keys) {
            String msg = UUID.randomUUID().toString();
            channel.basicPublish(EXCHANGE_NAME, routing_key, null, msg.getBytes());
            System.out.println(" [x] Sent routingKey = " + routing_key + ",msg = " +msg+ ".");
        }

        channel.close();
        connection.close();
    }

}
