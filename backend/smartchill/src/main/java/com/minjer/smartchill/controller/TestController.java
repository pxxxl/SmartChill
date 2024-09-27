package com.minjer.smartchill.controller;

import com.minjer.smartchill.entity.dto.Account;
import com.minjer.smartchill.entity.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Slf4j
@RestController("testController")
@RequestMapping("/test")
public class TestController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping()
    public Result test() {
        log.info("test");

        Account account = new Account();
        account.setUserId(1);
        account.setUserName("minjer");
        account.setPassword("123456");
        account.setSalt("salt");

        ArrayList<Account> accounts = new ArrayList<>();

        accounts.add(account);
        accounts.add(account);

        redisTemplate.opsForValue().set("account", accounts);

        ArrayList<Account> account1 = (ArrayList<Account>) redisTemplate.opsForValue().get("account");

        log.info("account1: " + account1);

        return Result.success(account1);
    }
}
