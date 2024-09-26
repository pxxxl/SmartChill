package com.minjer.smartchill.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("fridgeController")
@RequestMapping("/admin/drink")
public class FridgeController {
    @GetMapping("/basic")
    public String basic() {
        log.info("GetRequest: /admin/drink/basic");
        return "basic";
    }
}
