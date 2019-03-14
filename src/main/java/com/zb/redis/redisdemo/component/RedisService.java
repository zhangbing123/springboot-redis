package com.zb.redis.redisdemo.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * @description: redis操作类
 * @author: zhangbing
 * @create: 2019-03-14 15:44
 **/
@Component
@Slf4j
public class RedisService {
    @Autowired
    private Jedis jedis;

    /**
     * 根据key获取相应的值
     * @param key
     * @return
     */
    public Object get(String key){
        String str=null;
        try {
            str=jedis.get(key);
        }catch (Exception ex){
            log.error("getRedis:{Key:"+key+"}",ex);
        }
        return str;
    }

    /**
     * 根据key值设置value 如果value存在则覆盖
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key,Object value){
        try {
            String str=jedis.set(key, String.valueOf(value));
            if("OK".equals(str))
                return true;
        }catch (Exception ex){
            log.error("setToRedis:{Key:"+key+",value"+value+"}",ex);
        }

        return false;
    }
}
