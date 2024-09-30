package com.minjer.smartchill.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface TransactionMapper {
    @Insert("insert into transaction(drink_id, type, count, time) values(#{drinkId}, #{type}, #{count}, #{time})")
    void insertTransaction(Integer drinkId, Byte type, Integer count, LocalDateTime time);
}
