package com.zb.redis.redisdemo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: 用户控制器
 * @author: zhangbing
 * @create: 2019-03-14 16:09
 **/
@Log4j2
@RestController
@RequestMapping(value = RedisTestController.BASE_URL)
public class RedisTestController {

    public static final String BASE_URL = "redis/";


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/testSentinel")
    public void testSentinel() {
        int i = 1;
        while (true) {
            try {
                stringRedisTemplate.opsForValue().set("zhangbing" + i, i + "");
                System.out.println("设置key：" + "zhangbing" + i);
                i++;
                Thread.sleep(2000);
            } catch (Exception e) {
                log.error("异常:" + e);
            }

        }
    }

}
