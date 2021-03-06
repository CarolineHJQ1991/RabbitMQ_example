package com.caroline.wq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * User: Caroline.Han
 * Date: 2016-12-21
 * Time: 下午4:48
 */
public class Work {

    private final static String QUEUE_NAME = "workqueue";

    public static void main(String[] argv) throws IOException, TimeoutException, InterruptedException {
        //区分不同工作进程的输出   TODO:这句话是什么意思
        int hashCode = Work.class.hashCode();
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(hashCode + " [*] Waiting for messages. To exit press CTRL+C");

        //设置最大服务转发消息数量
        int prefetchCount = 1;
        channel.basicQos(prefetchCount);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //指定消费队列
        boolean ack = false; //打开应答机制
        channel.basicConsume(QUEUE_NAME, ack, consumer);
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println(hashCode + " [x] Received '" + message + "'");
            doWork(message);
            System.out.println(hashCode + " [x] Done");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    /**
     * 每个点耗时1s
     * @param task
     * @throws InterruptedException
     */
    private static void doWork(String task) throws InterruptedException {
        for (char ch : task.toCharArray()){
            if (ch == '.')
                Thread.sleep(1000);
        }
    }


    /**
     * TODO:我在执行任务的过程中,服务停止, 是先把原有的任务执行完
     * TODO:才会报错
     * TODO:工作者1和工作者2的hashcode是一样的
     */
}
