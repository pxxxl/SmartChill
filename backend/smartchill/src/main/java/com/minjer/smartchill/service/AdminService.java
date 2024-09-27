package com.minjer.smartchill.service;

import com.minjer.smartchill.entity.dto.Drink;
import com.minjer.smartchill.entity.pojo.DrinkCountInfo;
import com.minjer.smartchill.entity.result.Result;

import java.util.ArrayList;

public interface AdminService {
    public Result login(String username, String password);

    public Result register(String username, String password);

    public Result getDevicesInfo();

    Result getBasicDrinkInfo();

    Result putBasicDrinkInfo(Drink drink);

    Result addDrink(ArrayList<DrinkCountInfo> drinkCountInfos);
}
