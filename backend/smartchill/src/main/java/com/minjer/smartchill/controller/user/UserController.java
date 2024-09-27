package com.minjer.smartchill.controller.user;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("userController")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/drink")
    public Result getOnSaleDrinkPage(@RequestParam("page") Integer page,
                                     @RequestParam("pagesize") Integer size) {
        return userService.getOnSaleDrinkPage(page, size);
    }
}
