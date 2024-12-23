package com.minjer.smartchill.utils;

import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "pics")
public class ImageUtil {
    @Value("${pics.url}")
    private String url;

    @Value("${pics.token}")
    private String token;

    public String uploadImage(File file) {
        String uploadUrl = url + "/upload";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(uploadUrl);

        httpPost.setHeader("Authorization", token);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file);

        httpPost.setEntity(builder.build());

        try {
            // 发送请求
            CloseableHttpResponse response = httpClient.execute(httpPost);
            log.info("上传图片返回状态码: {}", response.getStatusLine().getStatusCode());

            // 获取返回结果
            HttpEntity responseEntity = response.getEntity();
            String res = EntityUtils.toString(responseEntity);
            log.info("上传图片返回结果: {}", res);

            // 提取图片 URL
            String imageUrl = res.substring(res.indexOf("http"), res.indexOf("\"}"));
            log.info("图片 URL: {}", imageUrl);

            response.close();
            httpClient.close();

            return imageUrl;
        } catch (Exception e) {
            log.error("上传图片失败", e);
            throw new BaseException(ResultEnum.UPLOAD_FAILED);
        }
    }

}
