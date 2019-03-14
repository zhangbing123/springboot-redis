package com.zb.redis.redisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisdemoApplication.class, args);
        System.out.println("============服务启动成功================");
    }

}
