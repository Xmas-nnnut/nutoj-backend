package com.xqj.nutoj.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author xqj
 * CreateTime 2023/6/27 22:50
 */
@Data
@ConfigurationProperties(prefix = "spring.redis")
@Configuration
public class RedissonConfig {
    private Integer database;
    private Integer rateLimitDatabase;
    private String host;
    private Integer port;
    private String password;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Bean
    @Qualifier("rateLimitRedissonClient")
    public RedissonClient rateLimitRedissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(rateLimitDatabase) // 使用限流数据数据库
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
