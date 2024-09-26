package com.minjer.smartchill.controller;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.utils.AccountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("testController")
@RequestMapping("/test")
public class TestController {
    @GetMapping()
    public Result test() {
        log.info("test");
        String salt = AccountUtil.generateSalt();
        return Result.success(salt);
    }
}
