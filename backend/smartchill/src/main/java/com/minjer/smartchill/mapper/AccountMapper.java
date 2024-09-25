package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    public void createAccount(String userName, String password, String salt);

    public Boolean checkAccount(String userName, String password);

    public String getSalt(String userName);

    public Account findByUserName(String userName);
}
