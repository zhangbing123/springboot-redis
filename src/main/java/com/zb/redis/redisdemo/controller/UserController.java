package com.zb.redis.redisdemo.controller;

import com.zb.redis.redisdemo.service.UserService;
import com.zb.redis.redisdemo.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Random;

/**
 * @description: 用户控制器
 * @author: zhangbing
 * @create: 2019-03-14 16:09
 **/
@RestController
@RequestMapping(value = UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL = "user/";

    @Autowired
    private UserService userService;

//    @RequestMapping(value = "setRedis",method = RequestMethod.GET)
//    public void setRedis(){
//        userService.setRedis("name","zhangbing");
//    }
//
//    @RequestMapping(value = "getRedis",method = RequestMethod.GET)
//    public String getRedis(){
//       String string = userService.getRedis("name");
//       return string;
//    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost", 6379);
        Date date = DateUtil.parseDate("2020-01-01", DateUtil.DATE_FORMAT_DATE);
        for (int k = 0; k < 365; k++) {

            date = DateUtil.offsiteDay(date, 1);
            String key = DateUtil.getTime(date, DateUtil.DATE_FORMAT_DATE);

            System.out.println("日期：=====================================" + key);

            for (int i = 1; i <= 100000; i++) {
                int coin = new Random().nextInt(2);
                if (coin==1){
                    jedis.setbit(key, i, String.valueOf(coin));
                }
            }

            System.out.println("结束=============================================");
        }


    }
}
