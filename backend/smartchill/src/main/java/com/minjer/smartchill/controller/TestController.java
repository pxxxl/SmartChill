package com.minjer.smartchill.controller;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("testController")
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @GetMapping()
    public Result test() {
        log.info("test");
        userService.sellDrink(4, 1);
        return Result.success();
    }
}
