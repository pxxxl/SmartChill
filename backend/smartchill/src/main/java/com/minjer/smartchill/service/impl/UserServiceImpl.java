package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.constant.RedisConstant;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.entity.vo.DrinkOnSale;
import com.minjer.smartchill.mapper.TemperatureMapper;
import com.minjer.smartchill.service.AdminService;
import com.minjer.smartchill.service.RedisService;
import com.minjer.smartchill.service.UserService;
import com.minjer.smartchill.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Override
    public Result getOnSaleDrinkPage(Integer page, Integer size) {
        // 1. 检查缓存上一次更新时间
        LocalDateTime lastUpdateTime = (LocalDateTime) redisService.get(RedisConstant.DRINK_UPDATE_TIME);

        // 2. 如果超过一分钟，更新缓存
        if (lastUpdateTime == null || lastUpdateTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            adminService.updateDrinkOnSale();
        }

        ArrayList<DrinkOnSale> drinkOnSaleList = (ArrayList<DrinkOnSale>) redisService.get(RedisConstant.DRINK_LIST);

        // 3. 分页返回
        List<DrinkOnSale> pageResult = PageUtil.paginate(page, size, drinkOnSaleList);

        int total = drinkOnSaleList.size();

        Map<String, Object> data = Map.of("total", total, "drinks", pageResult);

        return Result.success(data);
    }

    @Override
    public Result getTemperature() {
        // 1. 检查缓存上一次更新时间
        LocalDateTime lastUpdateTime = (LocalDateTime) redisService.get(RedisConstant.TEMPERATURE_UPDATE_TIME);

        // 2. 如果超过一分钟，更新缓存
        if (lastUpdateTime == null || lastUpdateTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            Double innerTemperature = temperatureMapper.getRecentInsideTemperature().doubleValue();
            Double outerTemperature = temperatureMapper.getRecentOutsideTemperature().doubleValue();

            redisService.set(RedisConstant.INNER_TEMPERATURE, innerTemperature);
            redisService.set(RedisConstant.OUTER_TEMPERATURE, outerTemperature);
            redisService.set(RedisConstant.TEMPERATURE_UPDATE_TIME, LocalDateTime.now());
        }

        // 3. 返回温度
        Double innerTemperature = (Double) redisService.get(RedisConstant.INNER_TEMPERATURE);
        Double outerTemperature = (Double) redisService.get(RedisConstant.OUTER_TEMPERATURE);

        Map<String, Object> data = Map.of("inner", innerTemperature, "outer", outerTemperature);

        return Result.success(data);
    }
}
