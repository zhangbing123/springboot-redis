package com.zb.redis.redisdemo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

/**
 * @description: redis配置类
 * @author: zhangbing
 * @create: 2019-03-14 15:33
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Bean
   public Jedis setRedisTemplate(){
        return new Jedis(host,port);
   }


}
