package com.zb.redis.redisdemo.service.impl;

import com.zb.redis.redisdemo.component.RedisService;
import com.zb.redis.redisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @description: 用户service
 * @author: zhangbing
 * @create: 2019-03-14 15:49
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisService redisService;

    @Override
    public void getRedis(String key) {
        System.out.println(redisService.get(key));
    }

    @Override
    public void setRedis(String key, String value) {
        redisService.set(key,value);
    }
}
