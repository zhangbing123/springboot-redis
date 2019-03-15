package com.zb.redis.redisdemo.config;

import com.zb.redis.redisdemo.helper.RedisManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * test
 * @description: redis配置类
 * @author: zhangbing
 * @create: 2019-03-14 15:33
 **/
@Configuration
@ConfigurationProperties(prefix = "spring.redis.cluster")
public class RedisConfig {

    private String nodes;

    private Integer commandTimeout;

    public String getNodes() {
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public Integer getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(Integer commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    @Bean
    public JedisCluster getJedisCluster(){
        String [] serverArray= nodes.split(",");
        Set<HostAndPort> nodes=new HashSet<>();

        for (String ipPort:serverArray){
            String [] ipPortPair=ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(),Integer.valueOf(ipPortPair[1].trim())));
        }
        JedisCluster jedisCluster = new JedisCluster(nodes,commandTimeout);
        return jedisCluster;
    }

    @Bean
    public RedisManager getRedisManager(){
        return new RedisManager(getJedisCluster());
    }

}
