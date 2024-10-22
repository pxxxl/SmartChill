package com.minjer.smartchill.controller.admin;

import com.minjer.smartchill.entity.dto.Drink;
import com.minjer.smartchill.entity.pojo.DrinkCountInfo;
import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController("fridgeController")
@RequestMapping("/admin/drink")
public class FridgeController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/basic")
    public Result getBasicDrinkInfo() {
        log.info("GetRequest: /admin/drink/basic");

        return adminService.getBasicDrinkInfo();
    }

    @PutMapping("/basic")
    public Result putBasicDrinkInfo(@RequestBody Drink drink) {
        log.info("PutRequest: /admin/drink/basic");

        return adminService.putBasicDrinkInfo(drink);
    }

    @PostMapping
    public Result postDrink(@RequestBody ArrayList<DrinkCountInfo> drinkCountInfos) {
        log.info("PostRequest: /admin/drink");
        log.info("drinkCountInfos: {}", drinkCountInfos);
        return adminService.addDrink(drinkCountInfos);
    }
}
