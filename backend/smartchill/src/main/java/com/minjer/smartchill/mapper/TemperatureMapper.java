package com.minjer.smartchill.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface TemperatureMapper {
    @Select("SELECT temp FROM temperature WHERE temperature.type = 1 ORDER BY time DESC LIMIT 1")
    public BigDecimal getRecentOutsideTemperature();

    @Select("SELECT temp FROM temperature WHERE temperature.type = 0 ORDER BY time DESC LIMIT 1")
    public BigDecimal getRecentInsideTemperature();

    @Insert("INSERT INTO temperature (temp, type, time) VALUES (#{temp}, #{type}, NOW())")
    public void insertTemperature(BigDecimal temp, Integer type);
}
