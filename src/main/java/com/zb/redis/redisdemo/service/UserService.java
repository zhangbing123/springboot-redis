package com.zb.redis.redisdemo.service;

public interface UserService {

    public String getRedis(String key);

    public void setRedis(String key, String value);
}
