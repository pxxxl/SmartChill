package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.constant.RedisConstant;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.RedisService;
import com.minjer.smartchill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisService redisService;

    @Override
    // TODO 未完成
    public Result getOnSaleDrinkPage(Integer page, Integer size) {
        // 1. 检查缓存上一次更新时间
        LocalDateTime lastUpdateTime = (LocalDateTime) redisService.get(RedisConstant.DRINK_UPDATE_TIME);

        // 2. 如果超过一分钟，更新缓存
        if (lastUpdateTime == null || lastUpdateTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            // 2.1 从数据库中获取数据
            // 2.2 更新缓存
        }else {
            // 3. 从缓存中获取数据
            return Result.success(redisService.get(RedisConstant.DRINK_LIST));
        }

        return null;
    }
}
