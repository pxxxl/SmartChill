package com.minjer.smartchill.service;

import com.minjer.smartchill.entity.result.Result;

public interface AdminService {
    public Result login(String username, String password);

    public Result register(String username, String password);
}
