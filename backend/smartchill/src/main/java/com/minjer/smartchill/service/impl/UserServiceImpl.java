package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.constant.RedisConstant;
import com.minjer.smartchill.entity.dto.Transaction;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.entity.vo.DrinkOnSale;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.DrinkMapper;
import com.minjer.smartchill.mapper.TemperatureMapper;
import com.minjer.smartchill.mapper.TransactionMapper;
import com.minjer.smartchill.service.AdminService;
import com.minjer.smartchill.service.RedisService;
import com.minjer.smartchill.service.UserService;
import com.minjer.smartchill.utils.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private RedisService redisService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private DrinkMapper drinkMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Override
    public Result getOnSaleDrinkPage(Integer page, Integer size, Boolean order, Integer fridge) {
        log.info("分页获取在售饮品列表");
        ArrayList<DrinkOnSale> drinkOnSaleList = null;

        // 1. 检查缓存上一次更新时间
        LocalDateTime lastUpdateTime = (LocalDateTime) redisService.get(RedisConstant.DRINK_UPDATE_TIME);

        // 2. 如果超过一分钟，更新缓存
        if (lastUpdateTime == null || lastUpdateTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            adminService.updateDrinkOnSale();
        }

        drinkOnSaleList = (ArrayList<DrinkOnSale>) redisService.get(RedisConstant.DRINK_LIST);
        if (drinkOnSaleList == null) {
            log.error("缓存中没有饮品信息，从数据库中获取");
            adminService.updateDrinkOnSale();
            drinkOnSaleList = (ArrayList<DrinkOnSale>) redisService.get(RedisConstant.DRINK_LIST);
        }

        List<DrinkOnSale> pageResult = null;
        int total = drinkOnSaleList.size();

        if (fridge != null || order) {
            ArrayList<Transaction> transactions = transactionMapper.getDrinkOnSaleByFridgeAndOrder(fridge, order);
            total = transactions.size();
            pageResult = new ArrayList<>();
            for (int i = (page - 1) * size, j = 0; i < transactions.size() && j < size; i++, j++) {
                Transaction transaction = transactions.get(i);

                for (DrinkOnSale onSale : drinkOnSaleList) {
                    if (onSale.getDrinkId().equals(transaction.getDrinkId())
                            && onSale.getFridge().equals(transaction.getFridge())
                            && onSale.getPosition().equals(transaction.getPosition())) {
                        pageResult.add(onSale);
                        break;
                    }
                }
            }

        } else {
            pageResult = PageUtil.paginate(page, size, drinkOnSaleList);
        }

        log.info("获取在售饮品列表成功，共{}种饮品", drinkOnSaleList.size());



        Map<String, Object> data = Map.of("total", total, "drinks", pageResult);

        return Result.success(data);
    }

    @Override
    public Result getTemperature() {
        log.info("获取温度信息");
        // 1. 检查缓存上一次更新时间
        LocalDateTime lastUpdateTime = (LocalDateTime) redisService.get(RedisConstant.TEMPERATURE_UPDATE_TIME);

        // 2. 如果超过一分钟，更新缓存
        if (lastUpdateTime == null || lastUpdateTime.plusMinutes(1).isBefore(LocalDateTime.now())) {
            log.info("温度信息缓存过期，从数据库中获取");
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

    @Override
    public Result sellDrink(Integer fridge, Integer position, Integer count) {
        // 1. 从缓存中获取在售饮品信息
        ArrayList<DrinkOnSale> drinkOnSales = (ArrayList<DrinkOnSale>) redisService.get(RedisConstant.DRINK_LIST);

        // 2. 更新饮品信息
        if (drinkOnSales != null) {
            for (DrinkOnSale drinkOnSale : drinkOnSales) {
                if (drinkOnSale.getPosition().equals(position) && drinkOnSale.getFridge().equals(fridge)) {
                    if (drinkOnSale.getCount() < count) {
                        throw new BaseException(ResultEnum.OUT_OF_STOCK);
                    }

                    drinkOnSale.setCount(drinkOnSale.getCount() - count);

                    if (drinkOnSale.getCount() == 0) {
                        drinkOnSales.remove(drinkOnSale);
                    }

                    transactionMapper.insertTransaction(drinkOnSale.getDrinkId(), (byte) 1, fridge, count, position, LocalDateTime.now());

                    log.info("饮品{}销售成功，剩余库存{}", drinkOnSale.getName(), drinkOnSale.getCount());
                    break;
                }
            }
        }

        // 3. 更新缓存
        if (drinkOnSales != null) {
            redisService.set(RedisConstant.DRINK_LIST, drinkOnSales);
        }

        return Result.success();
    }
}
