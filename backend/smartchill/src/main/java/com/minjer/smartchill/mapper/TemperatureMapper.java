package com.minjer.smartchill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface TemperatureMapper {
    @Select("SELECT temp FROM temperature WHERE temperature.type = 1 ORDER BY time DESC LIMIT 1")
    public BigDecimal getRecentOutsideTemperature();
}
