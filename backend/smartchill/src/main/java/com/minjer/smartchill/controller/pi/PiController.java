package com.minjer.smartchill.controller.pi;

import com.minjer.smartchill.entity.vo.TemperatureVO;
import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import com.minjer.smartchill.mapper.CameraMapper;
import com.minjer.smartchill.mapper.TemperatureMapper;
import com.minjer.smartchill.mapper.TransactionMapper;
import com.minjer.smartchill.service.UserService;
import com.minjer.smartchill.utils.RecognizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@RestController("piController")
@RequestMapping("/node")
public class PiController {
    private static final String INNER_ID = "2";
    private static final String OUTER_ID = "3";

    @Autowired
    private TemperatureMapper temperatureMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private CameraMapper cameraMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/temperature")
    public void postTemperature(@RequestBody TemperatureVO temperatureVO) {
        log.info("PostRequest: /node/temperature");
        log.info("temperatureVO: {}", temperatureVO);

        if (temperatureVO.getTemperature() == null || temperatureVO.getId() == null) {
            log.error("Temperature or id is null");
            return;
        }

        BigDecimal temperature = new BigDecimal(temperatureVO.getTemperature());
        if (temperature.compareTo(new BigDecimal(0)) < 0) {
            log.error("Temperature is less than 0");
            return;
        }

        if (INNER_ID.equals(temperatureVO.getId())) {
            temperatureMapper.insertTemperature(temperature, 0);
        } else if (OUTER_ID.equals(temperatureVO.getId())) {
            temperatureMapper.insertTemperature(temperature, 1);
        }
    }

    @PostMapping("/image")
    public void postImage(MultipartFile file) {
        log.info("PostRequest: /node/image");
        // 读取 MultipartFile 的字节数据
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new BaseException(ResultEnum.IO_ERROR);
        }

        String base64Image = "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(bytes);

        // 存入数据库
        cameraMapper.insertPhoto(UUID.randomUUID().toString(), base64Image, LocalDateTime.now());

        // 识别
        Boolean result = RecognizeUtil.recognize(file);
        log.info("Recognize result: {}", result);

        // 检测到商品售出
        if (result) {
            log.info("Recognize success");
            // TODO 映射相机和冰箱的关系
            int fridgeId = 1;
            int position = 1;
//            Integer drinkId = transactionMapper.getDrinkIdByPositionInteger(fridgeId, position);
//            if (drinkId != null) {
//                log.info("DrinkId: {} sold", drinkId);
//                transactionMapper.insertTransaction(drinkId, (byte) 1, fridgeId, 1, position, LocalDateTime.now());
//            }
            userService.sellDrink(fridgeId, position, 1);
        }
    }
}
