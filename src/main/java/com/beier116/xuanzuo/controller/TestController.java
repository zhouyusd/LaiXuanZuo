package com.beier116.xuanzuo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @GetMapping
    public String test() {
        redisTemplate.opsForValue().set("hello", "world");
        return "Hello World!!!";
    }
}
