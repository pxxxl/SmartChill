package com.minjer.smartchill.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

@Mapper
public interface CameraMapper {
    @Insert("INSERT INTO camera (name, image, time) VALUES (#{name}, #{image}, #{time})")
    public void insertPhoto(String name, String image, LocalDateTime time);

    @Select("SELECT image FROM camera ORDER BY time DESC LIMIT 1")
    public String getRecentPhoto();
}
