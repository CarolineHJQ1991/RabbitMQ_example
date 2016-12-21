package com.caroline.wq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-21
 * Time: 下午3:19
 */
public class NewTask {
    //队列名称
    private final static String QUEUE_NAME = "workqueue";
    public static void main(String[] args) throws IOException, TimeoutException {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //声明消息为持久化, 即使在任务执行中,服务断掉, 消息会接着执行完
//        boolean durable = true;
//        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        //发送10条消息,依次在消息后面附加1-10个点
        for (int i = 0; i < 5; i++) {
            String dots = "";
            for (int j = 0; j <= i; j++) {
                dots += ".";
            }
            String message = "helloworld" + dots + dots.length();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        //需要标识我们的信息为持久化的。通过设置MessageProperties（implements BasicProperties）值为PERSISTENT_TEXT_PLAIN。
//            channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }
        channel.close();
        connection.close();
    }
}
