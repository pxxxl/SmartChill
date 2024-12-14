package com.minjer.smartchill.controller;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.mapper.CameraMapper;
import com.minjer.smartchill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController("testController")
@RequestMapping("/test")
public class TestController {
    private final ResourceLoader resourceLoader;

    public TestController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private CameraMapper cameraMapper;

    @GetMapping()
    public Result test() throws IOException {
        return Result.success(cameraMapper.getRecentPhoto());
    }
}
