package com.minjer.smartchill.controller.user;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController("userController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/drink")
    public Result getOnSaleDrinkPage(@RequestParam("page") Integer page,
                                     @RequestParam("pagesize") Integer size,
                                     @RequestParam("order") Boolean order,
                                     @RequestParam(value = "fridge", required = false) Integer fridge) {
        log.info("分页获取在售饮品列表, page: {}, size: {}, order: {}, fridge: {}", page, size, order, fridge);
        return userService.getOnSaleDrinkPage(page, size, order, fridge);
    }

    @GetMapping("/temperature")
    public Result getTemperature() {
        return userService.getTemperature();
    }
}
