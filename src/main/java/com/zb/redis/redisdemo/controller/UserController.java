package com.zb.redis.redisdemo.controller;

import com.zb.redis.redisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description: 用户控制器
 * @author: zhangbing
 * @create: 2019-03-14 16:09
 **/
@Controller
@RequestMapping(value = UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL="user/";

    @Autowired
    private UserService userService;

    @RequestMapping(value = "setRedis",method = RequestMethod.GET)
    public void setRedis(){
        userService.setRedis("name","zhangbing");
    }

    @RequestMapping(value = "getRedis",method = RequestMethod.GET)
    public void getRedis(){
        userService.getRedis("name");
    }
}
