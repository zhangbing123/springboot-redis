package com.zb.redis.redisdemo.service.impl;

import com.zb.redis.redisdemo.helper.RedisManager;
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

    private RedisManager redisManager = new RedisManager();

    @Override
    public String getRedis(String key) {
        return (String) redisManager.get(key);
    }

    @Override
    public void setRedis(String key, String value) {
        redisManager.set(key,value);
    }
}
