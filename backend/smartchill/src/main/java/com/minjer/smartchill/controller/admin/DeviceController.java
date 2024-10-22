package com.minjer.smartchill.controller.admin;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController("deviceController")
@RequestMapping("/admin/device")
public class DeviceController {

    @Autowired
    private AdminService adminService;

    @Deprecated
    @GetMapping
    public Result getDevicesInfo() {
        log.info("GetRequest: /admin/device");

        return adminService.getDevicesInfo();
    }

    @GetMapping("/status")
    public Result getDevicesStatus() {
        // TODO: 目前是写死的，等待接入真实数据 (Time: 2024/10/22 9:04)
        log.info("GetRequest: /admin/device/status");
        // 构造 fridge_1 items 列表
        List<Map<String, Object>> fridge1Items = new ArrayList<>();
        Map<String, Object> camera1Fridge1 = new HashMap<>();
        camera1Fridge1.put("name", "Camera 1");
        camera1Fridge1.put("id", "dwfiei");
        camera1Fridge1.put("position", "1");
        camera1Fridge1.put("status", "on");

        Map<String, Object> temp1Fridge1 = new HashMap<>();
        temp1Fridge1.put("name", "Temp 1");
        temp1Fridge1.put("id", "dwfiei");
        temp1Fridge1.put("position", "");
        temp1Fridge1.put("status", "off");

        fridge1Items.add(camera1Fridge1);
        fridge1Items.add(temp1Fridge1);

        // 构造 fridge_2 items 列表
        List<Map<String, Object>> fridge2Items = new ArrayList<>();
        Map<String, Object> camera1Fridge2 = new HashMap<>();
        camera1Fridge2.put("name", "Camera 1");
        camera1Fridge2.put("id", "dwfiei");
        camera1Fridge2.put("position", "1");
        camera1Fridge2.put("status", "on");

        Map<String, Object> temp1Fridge2 = new HashMap<>();
        temp1Fridge2.put("name", "Temp 1");
        temp1Fridge2.put("id", "dwfiei");
        temp1Fridge2.put("position", "");
        temp1Fridge2.put("status", "25℃");

        fridge2Items.add(camera1Fridge2);
        fridge2Items.add(temp1Fridge2);

        // 构造 fridge_1 和 fridge_2 的 Map
        Map<String, Object> fridge1 = new HashMap<>();
        fridge1.put("name", "fridge_1");
        fridge1.put("items", fridge1Items);

        Map<String, Object> fridge2 = new HashMap<>();
        fridge2.put("name", "fridge_2");
        fridge2.put("items", fridge2Items);

        // 构造 data 列表
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(fridge1);
        dataList.add(fridge2);

        return new Result(200, "目前为测试数据", dataList);
    }

}
