package com.minjer.smartchill.controller.admin;

import com.minjer.smartchill.entity.pojo.LoginInfo;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("accountController")
@RequestMapping("/admin")
public class AccountController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginInfo loginInfo) {
        log.info("login: " + loginInfo.toString());
        return adminService.login(loginInfo.getUsername(), loginInfo.getPassword());
    }

    @PostMapping("/register")
    public Result register(@RequestBody LoginInfo loginInfo) {
        log.info("register: " + loginInfo.toString());
        return adminService.register(loginInfo.getUsername(), loginInfo.getPassword());
    }
}
