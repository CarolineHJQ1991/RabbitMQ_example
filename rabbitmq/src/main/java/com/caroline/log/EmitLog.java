package com.caroline.log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-22
 * Time: 上午11:05
 */
public class EmitLog {

    private final static String EXCHANGE_NAME = "ex_log";

    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明转发器和类型
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String message = new Date() + " : log something";
        //发送消息改为发送到转发器
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes());

        System.out.println(" [x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }
}
