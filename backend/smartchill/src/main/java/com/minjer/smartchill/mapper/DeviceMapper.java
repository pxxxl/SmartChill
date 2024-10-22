package com.minjer.smartchill.mapper;

import com.minjer.smartchill.entity.vo.DeviceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface DeviceMapper {
    @Select("SELECT name, device_id, position, belong FROM device")
    public ArrayList<DeviceInfo> getDevicesInfo();
}
