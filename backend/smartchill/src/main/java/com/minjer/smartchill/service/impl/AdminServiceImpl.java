package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.constant.RedisConstant;
import com.minjer.smartchill.entity.dto.Account;
import com.minjer.smartchill.entity.dto.Drink;
import com.minjer.smartchill.entity.dto.Transaction;
import com.minjer.smartchill.entity.pojo.DrinkCountInfo;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.entity.vo.DeviceInfo;
import com.minjer.smartchill.entity.vo.DrinkOnSale;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.*;
import com.minjer.smartchill.service.AdminService;
import com.minjer.smartchill.service.RedisService;
import com.minjer.smartchill.utils.AccountUtil;
import com.minjer.smartchill.utils.JwtUtil;
import com.minjer.smartchill.utils.TemperatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DrinkMapper drinkMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Autowired
    private TransactionMapper transactionMapper;
    
    @Autowired
    private CameraMapper cameraMapper;

    @Override
    public Result login(String username, String password) {
        // 检验用户是否存在
        Account account = accountMapper.findByUserName(username);

        if (account == null) {
            log.error("{}用户不存在", username);
            throw new BaseException(ResultEnum.ACCOUNT_NOT_EXIST);
        }

        // 检验密码是否正确
        if (!accountMapper.checkAccount(username, AccountUtil.hashPassword(password, account.getSalt()))) {
            log.error("{}密码错误", username);
            throw new BaseException(ResultEnum.PASSWORD_ERROR);
        }

        // 登录成功, 返回token
        String token = JwtUtil.generateToken(account.getUserName());
        log.info("{}登录成功, token: {}", username, token);

        // 构建Map
        Map<String, Object> map = new HashMap<>();
        map.put("Authorization", token);

        return Result.success(map);
    }

    @Override
    public Result register(String username, String password) {
        // 检验用户是否存在
        Account account = accountMapper.findByUserName(username);

        if (account != null) {
            throw new BaseException(ResultEnum.ACCOUNT_EXIST);
        }

        // 生成盐
        String salt = AccountUtil.generateSalt();

        // 生成密码
        String hashPassword = AccountUtil.hashPassword(password, salt);

        // 创建用户
        accountMapper.createAccount(username, hashPassword, salt);

        log.info("{}注册成功", username);

        return Result.success();
    }

    @Override
    public Result getDevicesInfo() {
        // 1. 获取设备信息
        ArrayList<DeviceInfo> devicesInfo = deviceMapper.getDevicesInfo();

        // 2. 构建返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("device", devicesInfo);

        log.info("获取设备信息成功");

        return Result.success(map);
    }

    @Override
    public Result getBasicDrinkInfo() {
        // 1. 获取饮品信息
        ArrayList<Drink> basicDrinkInfo = drinkMapper.getDrinksInfo();

        // 2. 构建返回结果
        Map<String, Object> map = new HashMap<>();
        map.put("drinks", basicDrinkInfo);

        log.info("获取饮品信息成功");

        return Result.success(map);
    }

    @Override
    public Result putBasicDrinkInfo(Drink drink) {
        // 1. 检查饮品是否存在
        if (drink.getName() == null) {
            log.error("饮品不存在");
            throw new BaseException(ResultEnum.DRINK_NOT_EXIST);
        }

        // 2. 检查饮品价格是否合法
        if (drink.getPrice().compareTo(BigDecimal.valueOf(0.0)) <= 0 || drink.getPrice().compareTo(BigDecimal.valueOf(100.0)) > 0) {
            log.error("饮品{}价格不合法, price: {}", drink.getName(), drink.getPrice());
            throw new BaseException(ResultEnum.PRICE_ERROR);
        }

        Drink drinkInfo = drinkMapper.getDrinkByName(drink.getName());
        if (drinkInfo == null) {
            // 2. 创建饮品信息
            drinkMapper.insertDrink(drink.getName(), drink.getPrice(), drink.getImage());
            log.info("创建饮品信息成功, name: {}, price: {}, image: {}", drink.getName(), drink.getPrice(), drink.getImage());
        } else {
            // 2. 更新饮品信息
            drinkMapper.updateDrink(drink.getName(), drink.getPrice(), drink.getImage());
            log.info("更新饮品信息成功, name: {}, price: {}, image: {}", drink.getName(), drink.getPrice(), drink.getImage());
        }

        return Result.success();
    }

    @Override
    @Transactional
    public Result addDrink(ArrayList<DrinkCountInfo> drinkCountInfos) {
        // 从缓存中获取在售饮品信息
        ArrayList<DrinkOnSale> drinkOnSales = (ArrayList<DrinkOnSale>) redisService.get("drinkList");

        // 对提交的补货信息逐个检查
        for (DrinkCountInfo drinkCountInfo : drinkCountInfos) {
            log.info("补货信息：{}", drinkCountInfo);
            // 0. 检查补货信息是否合法
            if (drinkCountInfo.getCount() <= 0 || drinkCountInfo.getPosition() <= 0 || drinkCountInfo.getFridge() <= 0) {
                log.error("补货信息不合法, count: {}, position: {}, fridge: {}", drinkCountInfo.getCount(), drinkCountInfo.getPosition(), drinkCountInfo.getFridge());
                throw new BaseException(ResultEnum.UNPROCESABLE_ENTITY);
            } else if (drinkCountInfo.getDrinkId() == null) {
                log.error("饮品ID为空");
                throw new BaseException(ResultEnum.UNPROCESABLE_ENTITY);
            }

            // 1. 检查饮品是否存在
            Drink drink = drinkMapper.getDrinkById(drinkCountInfo.getDrinkId());

            if (drink == null) {
                log.error("饮品不存在, drinkId: {}", drinkCountInfo.getDrinkId());
                throw new BaseException(ResultEnum.DRINK_NOT_EXIST);
            }

            // 2. 判断该冰箱该位置是否已经售空
            if (drinkOnSales != null) {
                for (DrinkOnSale drinkOnSale : drinkOnSales) {
                    if (drinkOnSale.getFridge().equals(drinkCountInfo.getFridge())
                            && drinkOnSale.getPosition().equals(drinkCountInfo.getPosition())
                            && drinkOnSale.getCount() != 0) {
                        log.error("饮品{}未售空, fridge: {}, position: {}，不允许补货", drink.getName(), drinkCountInfo.getFridge(), drinkCountInfo.getPosition());
                        throw new BaseException(ResultEnum.DRINK_UNSOLD);
                    }
                }
            } else {
                // 2.1 如果缓存中没有饮品信息，则进行初始化写入
                drinkOnSales = new ArrayList<>();
            }

            // 3. 更新饮品库存
            DrinkOnSale drinkOnSale = new DrinkOnSale();

            drinkOnSale.setDrinkId(drink.getId());
            drinkOnSale.setName(drink.getName());
            drinkOnSale.setPrice(drink.getPrice());
            drinkOnSale.setFridge(drinkCountInfo.getFridge());
            drinkOnSale.setPosition(drinkCountInfo.getPosition());
            drinkOnSale.setCount(drinkCountInfo.getCount());
            drinkOnSale.setImage(drink.getImage());
            drinkOnSale.setCreateTime(LocalDateTime.now());
            // 设定温度
            BigDecimal temperature = temperatureMapper.getRecentOutsideTemperature();
            drinkOnSale.setTemperature(temperature);
            drinkOnSale.setCreateTemperature(temperature);

            drinkOnSales.add(drinkOnSale);

            // 4. 更新交易表
            transactionMapper.insertTransaction(drink.getId(), (byte) 0, drinkCountInfo.getFridge(), drinkCountInfo.getCount(), drinkCountInfo.getPosition(), LocalDateTime.now());

            log.info("饮品{}补货成功, count: {}, fridge: {}, position: {}", drink.getName(), drinkCountInfo.getCount(), drinkCountInfo.getFridge(), drinkCountInfo.getPosition());
        }

        // 4. 更新缓存
        if (drinkOnSales != null) {
            redisService.set(RedisConstant.DRINK_LIST, drinkOnSales);
        }

        return Result.success();
    }

    @Override
    public void updateDrinkOnSale() {
        log.info("更新饮品信息（温度、图片、价格），当前时间：{}", LocalDateTime.now());
        // 1. 从缓存中获取在售饮品信息
        ArrayList<DrinkOnSale> drinkOnSales = (ArrayList<DrinkOnSale>) redisService.get(RedisConstant.DRINK_LIST);

        // 2. 更新饮品信息
        if (drinkOnSales != null) {
            log.info("更新在售饮品信息，当前剩余情况：{}", drinkOnSales);
            // 2.1 更新最新的温度
            BigDecimal temperature = temperatureMapper.getRecentInsideTemperature();

            ArrayList<DrinkOnSale> newDrinkOnSales = new ArrayList<>();

            for (DrinkOnSale drinkOnSale : drinkOnSales) {
                long minutes = Duration.between(drinkOnSale.getCreateTime(), LocalDateTime.now()).toMinutes();
                BigDecimal updateTemperature = BigDecimal.valueOf(TemperatureUtil.calCoolingTemperature(drinkOnSale.getCreateTemperature().doubleValue(), temperature.doubleValue(), minutes));

                // 更新饮料基本信息
                Drink drink = drinkMapper.getDrinkById(drinkOnSale.getDrinkId());
                drinkOnSale.setImage(drink.getImage());
                drinkOnSale.setPrice(drink.getPrice());

                // 将温度限定为一位小数
                updateTemperature = updateTemperature.setScale(1, RoundingMode.HALF_UP);
                drinkOnSale.setTemperature(updateTemperature);
                newDrinkOnSales.add(drinkOnSale);
            }

            // 2.2. 更新缓存
            redisService.set(RedisConstant.DRINK_LIST, newDrinkOnSales);
            redisService.set(RedisConstant.DRINK_UPDATE_TIME, LocalDateTime.now());
        } else {
            log.error("在售饮品信息为空，读取交易表进行初始化");
            // 2.3. 读取交易表进行初始化
            ArrayList<DrinkOnSale> newDrinkOnSales = new ArrayList<>();
            ArrayList<Transaction> drinkOnSaleList = transactionMapper.getDrinkOnSale();
            log.info("读取交易表成功，当前剩余情况：{}", drinkOnSaleList);

            // 2.4. 更新饮品信息
            for (Transaction transaction : drinkOnSaleList) {
                // 构建新的饮品信息
                DrinkOnSale drinkOnSale = new DrinkOnSale();
                // 获取饮品信息
                Drink drink = drinkMapper.getDrinkById(transaction.getDrinkId());

                drinkOnSale.setDrinkId(drink.getId());
                drinkOnSale.setName(drink.getName());
                drinkOnSale.setPrice(drink.getPrice());
                drinkOnSale.setFridge(transaction.getFridge());
                drinkOnSale.setPosition(transaction.getPosition());
                drinkOnSale.setCount(transaction.getCount());
                drinkOnSale.setImage(drink.getImage());
                drinkOnSale.setCreateTime(LocalDateTime.now());
                // 设定温度
                BigDecimal temperature = temperatureMapper.getRecentOutsideTemperature().setScale(1, RoundingMode.HALF_UP);
                drinkOnSale.setTemperature(temperature);
                drinkOnSale.setCreateTemperature(temperature);

                newDrinkOnSales.add(drinkOnSale);
            }

            // 2.5. 更新缓存
            redisService.set(RedisConstant.DRINK_LIST, newDrinkOnSales);
        }
    }

    @Override
    public Result getCamerasInfo() {
        // 1. 获取摄像头信息
        String recentPhoto = cameraMapper.getRecentPhoto();

        log.info("获取摄像头信息成功");

        return Result.success(recentPhoto);
    }

}
