package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.dto.Drink;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.ArrayList;

@Mapper
public interface DrinkMapper {
    @Select("SELECT * FROM drink_info")
    public ArrayList<Drink> getDrinksInfo();

    @Select("SELECT * FROM drink_info WHERE id = #{drinkId}")
    public Drink getDrinkById(Integer drinkId);

    @Insert("INSERT INTO drink_info (name, price, image) VALUES (#{name}, #{price}, #{image})")
    public void insertDrink(String name, BigDecimal price, String image);

    @Select("SELECT * FROM drink_info WHERE name = #{name}")
    public Drink getDrinkByName(String name);

    public void updateDrink(String name, BigDecimal price, String image);
}
