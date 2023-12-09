package com.xqj.nutoj.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static com.xqj.nutoj.constant.MqConstant.*;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
@Slf4j
@Component
public class InitRabbitMqBean {

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @PostConstruct
    public void init() {
        try {
            // 创建连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(host);
            // 建立连接、创建频道
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            // 创建交换机
            String exchangeName = EXCHANGE_NAME;
            channel.exchangeDeclare(exchangeName, DIRECT_EXCHANGE);
            // 创建队列
            String queueName = QUEUE;
            // 参数
            // durable: 是否开启持久化。消息队列重启后，消息是否丢失
            // exclusive: 是否允许当前这个创建消息队列的连接操作消息队列
            // autoDelete: 没有人用队列后，是否要删除队列
            channel.queueDeclare(queueName, true, false, false, null);
            // 交换机与消息队列进行绑定
            // routingKey: 控制消息要转发到哪个队列
            channel.queueBind(queueName, exchangeName, ROUTING_KEY);
            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败", e);
        }
    }
}
