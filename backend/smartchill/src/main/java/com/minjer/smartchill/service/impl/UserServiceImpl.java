package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result getOnSaleDrinkPage(Integer page, Integer size) {

        return null;
    }
}
