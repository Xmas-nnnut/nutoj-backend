package com.xqj.nutoj.mq;

import com.rabbitmq.client.Channel;
import com.xqj.nutoj.judge.JudgeService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.xqj.nutoj.constant.MqConstant.QUEUE;

@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private JudgeService judgeService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(SendMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        long questionSubmitId = message.getQuestionSubmitId();
        long userId = message.getUserId();
//        long questionSubmitId = Long.parseLong(message);
        try {
            judgeService.doJudge(questionSubmitId,userId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
