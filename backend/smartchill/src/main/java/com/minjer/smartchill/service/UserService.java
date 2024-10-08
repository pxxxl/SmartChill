package com.minjer.smartchill.service;

import com.minjer.smartchill.entity.result.Result;

public interface UserService {

    Result getOnSaleDrinkPage(Integer page, Integer size);

    Result getTemperature();

    Result sellDrink(Integer position, Integer count);
}
