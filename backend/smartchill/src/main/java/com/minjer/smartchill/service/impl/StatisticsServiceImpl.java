package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.entity.dto.Drink;
import com.minjer.smartchill.entity.dto.Transaction;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.entity.vo.DrinkStatisticsVO;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.DrinkMapper;
import com.minjer.smartchill.mapper.TransactionMapper;
import com.minjer.smartchill.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private DrinkMapper drinkMapper;

    @Override
    public Result getSellStatisticsByDate(LocalDate begin, LocalDate end) {
        // 1. 检查参数
        if (end.isBefore(begin)) {
            log.error("结束时间早于开始时间");
            throw new BaseException(ResultEnum.DATE_ERROR);
        }

        // 1.1 转换为LocalDateTime
        LocalDateTime beginTime = begin.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59, 59);

        // 2. 查询销售表
        log.info("查询销售表，开始时间: {}, 结束时间: {}", beginTime, endTime);
        ArrayList<Transaction> transactions = transactionMapper.getDrinkSellStatisticsByDate(beginTime, endTime);

        // 3. 将饮料id转化为名称
        ArrayList<Drink> drinkList = drinkMapper.getDrinksInfo();
        Map<Integer, String> drinkMap = new HashMap<>();
        for (Drink drink : drinkList) {
            drinkMap.put(drink.getId(), drink.getName());
        }
        int total = 0;

        ArrayList<DrinkStatisticsVO> drinks = new ArrayList<>();

        if (transactions != null) {
            for (Transaction transaction : transactions) {
                DrinkStatisticsVO drink = new DrinkStatisticsVO();
                drink.setName(drinkMap.get(transaction.getDrinkId()));
                drink.setCount(transaction.getCount());
                drinks.add(drink);
                total += transaction.getCount();
            }
            log.info("共查询到{}条销售记录", transactions.size());
        }else {
            log.info("没有查询到销售记录");
        }
        // 4. 封装返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("drinks", drinks);

        return Result.success(data);
    }
}
