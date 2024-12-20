package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.dto.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Mapper
public interface TransactionMapper {
    @Insert("insert into transaction(drink_id, type, fridge, count, position ,time) values(#{drinkId}, #{type}, #{fridge}, #{count}, #{position}, #{time})")
    void insertTransaction(Integer drinkId, Byte type, Integer fridge ,Integer count,Integer position, LocalDateTime time);

    ArrayList<Transaction> getDrinkOnSale();

    ArrayList<Transaction> getDrinkOnSaleByFridgeAndOrder(Integer fridge, Boolean order);

    @Select("select drink_id from transaction where fridge = #{fridge} and position = #{position} ORDER BY time DESC LIMIT 1")
    Integer getDrinkIdByPositionInteger(Integer fridge ,Integer position);

    ArrayList<Transaction> getDrinkSellStatisticsByDate(LocalDateTime begin, LocalDateTime end);
}
