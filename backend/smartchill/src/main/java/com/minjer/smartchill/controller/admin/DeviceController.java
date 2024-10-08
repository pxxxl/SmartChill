package com.minjer.smartchill.controller.admin;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("deviceController")
@RequestMapping("/admin/device")
public class DeviceController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public Result getDevicesInfo() {
        log.info("GetRequest: /admin/device");

        return adminService.getDevicesInfo();
    }
}
