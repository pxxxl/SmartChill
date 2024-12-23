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

    @GetMapping("/camera")
    public Result getCamerasInfo() {
        log.info("GetRequest: /admin/device/camera");

        return adminService.getCamerasInfo();
    }

    @GetMapping("/status")
    public Result getDevicesStatus() {
        log.info("GetRequest: /admin/device/status");

        ArrayList<HashMap<String, Object>> result = new ArrayList<>();

        HashMap<String, Object> fridge1 = new HashMap<>();
        fridge1.put("id", "1");
        fridge1.put("name", "Fridge");
        ArrayList<HashMap<String, Object>> fridgeItems = new ArrayList<>();

        HashMap<String, Object> item1 = new HashMap<>();
        item1.put("id", "2");
        item1.put("name", "TemperatureSensor_1");
        item1.put("status", "on");
        item1.put("position", "Inner");
        fridgeItems.add(item1);

        HashMap<String, Object> item2 = new HashMap<>();
        item2.put("id", "3");
        item2.put("name", "TemperatureSensor_2");
        item2.put("status", "on");
        item2.put("position", "Outer");
        fridgeItems.add(item2);

        HashMap<String, Object> item3 = new HashMap<>();
        item3.put("id", "4");
        item3.put("name", "Camera_1");
        item3.put("status", "on");
        item3.put("position", "1");
        fridgeItems.add(item3);

        fridge1.put("items", fridgeItems);

        result.add(fridge1);

        return Result.success(result);
    }

}
