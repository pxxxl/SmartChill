package com.minjer.smartchill;

import com.minjer.smartchill.mapper.TemperatureMapper;
import com.minjer.smartchill.mapper.TransactionMapper;
import com.minjer.smartchill.utils.ImageUtil;
import com.minjer.smartchill.utils.RecognizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
@Slf4j
@SpringBootTest
class SmartchillApplicationTests {
    @Autowired
    private ImageUtil imageUtil;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Test
    void testTransactionMapper() {
        String temp = "27.14";
        BigDecimal temperature = new BigDecimal(temp);
        temperatureMapper.insertTemperature(temperature, 1);
    }

    @Test
    void testRecognize() {
        File file = null;
        try {
            file = new ClassPathResource("static/image_1.jpg").getFile();
        } catch (IOException e) {
            log.info("file not found");

        }
        RecognizeUtil.recognize("3", file);
    }

    @Test
    void testPatchRecognize() {
        for (int i = 1; i <= 13; i++) {
            File file = null;
            try {
                file = new ClassPathResource("static/image_" + i + ".jpg").getFile();
            } catch (IOException e) {
                log.info("file not found");
            }
            RecognizeUtil.recognize("3", file);
        }
    }

}
