package com.minjer.smartchill;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com.minjer.smartchill.mapper")
@ServletComponentScan
@Slf4j
public class SmartchillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartchillApplication.class, args);
        log.info("SmartchillApplication started");
    }

}
