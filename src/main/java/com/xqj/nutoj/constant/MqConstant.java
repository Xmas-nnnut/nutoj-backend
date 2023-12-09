package com.xqj.nutoj.constant;

/**
 * @author xqj
 * CreateTime 2023/6/24 16:26
 * MQ 信息
 */
public interface MqConstant {

    /**
     * 普通交换机
     */
    String EXCHANGE_NAME = "test_code_exchange";
    String QUEUE = "test_code_queue";
    String ROUTING_KEY = "my_routingKey";
    String DIRECT_EXCHANGE = "direct";

    /**
     * 死信队列交换机
     */
    String DLX_EXCHANGE = "test_code-dlx-exchange";

    /**
     * 死信队列
     */
    String DLX_QUEUE = "test_code_dlx_queue";

    /**
     * 死信队列路由键
     */
    String DLX_ROUTING_KEY = "test_code_dlx_routingKey";
}
