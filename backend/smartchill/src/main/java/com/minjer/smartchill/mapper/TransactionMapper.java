package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.dto.Transaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Mapper
public interface TransactionMapper {
    @Insert("insert into transaction(drink_id, type, count, position ,time) values(#{drinkId}, #{type}, #{count}, #{position}, #{time})")
    void insertTransaction(Integer drinkId, Byte type, Integer count,Integer position, LocalDateTime time);

    ArrayList<Transaction> getDrinkOnSale();
}
