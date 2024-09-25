package com.minjer.smartchill.service.impl;

import com.minjer.smartchill.entity.dto.Account;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.AccountMapper;
import com.minjer.smartchill.service.AdminService;
import com.minjer.smartchill.utils.AccountUtil;
import com.minjer.smartchill.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public Result login(String username, String password) {
        // 检验用户是否存在
        Account account = accountMapper.findByUserName(username);

        if (account == null) {
            throw new BaseException(ResultEnum.ACCOUNT_NOT_EXIST);
        }

        // 检验密码是否正确
        if (!accountMapper.checkAccount(username, AccountUtil.hashPassword(password, account.getSalt()))) {
            throw new BaseException(ResultEnum.PASSWORD_ERROR);
        }

        // 登录成功, 返回token
        String token = JwtUtil.generateToken(account.getUserName());

        // 构建Map
        Map<String, Object> map = new HashMap<>();
        map.put("Token", token);

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

        return Result.success();
    }
}
