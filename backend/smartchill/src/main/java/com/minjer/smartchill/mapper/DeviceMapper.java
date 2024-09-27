package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.vo.DeviceInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface DeviceMapper {

    @Insert("INSERT INTO device (name, description) VALUES (#{name}, #{description})")
    public void insertDevice(String name, String description);

    @Select("SELECT name, description FROM device")
    public ArrayList<DeviceInfo> getDevicesInfo();
}
