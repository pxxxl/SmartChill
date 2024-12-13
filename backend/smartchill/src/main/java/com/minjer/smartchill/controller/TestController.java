package com.minjer.smartchill.controller;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.CameraMapper;
import com.minjer.smartchill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

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
        log.info("test");
        Resource resource = resourceLoader.getResource("classpath:static/p1.png");
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] bytes = inputStream.readAllBytes();
            String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
//            log.info("base64Image: {}", base64Image);
            cameraMapper.insertPhoto(String.valueOf(UUID.randomUUID()), base64Image, LocalDateTime.now());
            return Result.success(base64Image);
        } catch (IOException e) {
            throw new BaseException(ResultEnum.IO_ERROR);
        }


    }
}
