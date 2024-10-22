package com.minjer.smartchill.service;

import com.minjer.smartchill.entity.result.Result;

public interface UserService {

    Result getOnSaleDrinkPage(Integer page, Integer size, Boolean order, Integer fridge);

    Result getTemperature();

    Result sellDrink(Integer fridge, Integer position, Integer count);
}
