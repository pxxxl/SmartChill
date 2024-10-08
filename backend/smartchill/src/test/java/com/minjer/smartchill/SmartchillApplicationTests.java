package com.minjer.smartchill;

import com.minjer.smartchill.utils.ImageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class SmartchillApplicationTests {
    @Autowired
    private ImageUtil imageUtil;
    @Test
    void contextLoads() {
        String url = imageUtil.uploadImage(new File("C:\\Users\\Minjer\\Desktop\\PixPin_2024-10-08_19-53-12.png"));
        System.out.println(url);
    }

}
