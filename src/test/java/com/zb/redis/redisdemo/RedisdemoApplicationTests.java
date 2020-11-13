package com.zb.redis.redisdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisdemoApplicationTests {

    @Autowired
    private JedisSentinelPool pool;


    @Test
    public void contextLoads() {
        Jedis resource = pool.getResource();
        resource.set("name2", "zhangbing1234");
        Object name = resource.get("name2");
        System.out.println(name);
    }

}
