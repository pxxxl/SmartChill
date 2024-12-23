package com.minjer.smartchill.utils;

import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class RecognizeUtil {
    private static final String RECOGNIZE_URL = "http://192.168.5.2:5987/img";

    public static Boolean recognize(MultipartFile multipartFile) {
        log.info("Recognize: multipartFile={}", multipartFile.getOriginalFilename());
        try {
            File file = convert(multipartFile);
            return recognize("3", file);
        } catch (IOException e) {
            log.error("Recognize Exception: {}", e.getMessage());
            return false;
        }
    }

    public static Boolean recognize(String id, File file) {
        log.info("Recognize: id={}, file={}", id, file.getName());
        // 构建请求路径
        String url = RECOGNIZE_URL + "?cam_id=" + id;

        OkHttpClient client = new OkHttpClient();

        // 构建请求体
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), MultipartBody.create(file, MediaType.parse("image/jpg")))
                .build();


        // 构建请求
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .post(body)
                .build();
        log.info("Request: {}", request);
        String change = "";
        // 发送请求
        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("Recognize failed: {}", response);
                return false;
            }
            String result = response.body().string();
            log.info("Recognize result1: {}", result);


            // parse result
            log.info("Begin to parse result");
            Pattern pattern = Pattern.compile("\"change\":(-?\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                change = matcher.group(1);
            }

            log.info("Recognize change: {}", change);

            if (!change.equals("0")) {
                log.info("Recognize success - In");
                return true;
            }

        } catch (Exception e) {
            log.error("Recognize Exception: {}", e.getMessage());

            return false;
        }

        return false;
    }

    public static File convert(MultipartFile file) throws IOException {
        // 检查 MultipartFile 是否为空
        if (file.isEmpty()) {
            throw new BaseException(ResultEnum.FILE_EMPTY);
        }

        // 创建临时文件
        File convertedFile = File.createTempFile("temp", null);

        // 将 MultipartFile 的内容写入 File
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        }
        return convertedFile;
    }
}
