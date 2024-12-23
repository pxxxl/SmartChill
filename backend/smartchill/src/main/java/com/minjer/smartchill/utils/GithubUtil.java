package com.minjer.smartchill.utils;

import com.minjer.smartchill.enums.ResultEnum;
import com.minjer.smartchill.exception.BaseException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "github")
public class GithubUtil {
    @Value("${github.api}")
    private static String api;

    @Value("${github.token}")
    private static String token;

    // 提取指定文件夹中的图片，并上传到 GitHub，返回上传后的 URL 列表
    public static List<String> uploadImagesFromFolder(String folderPath) throws IOException {
        List<String> imageUrls = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".png") ||
                name.toLowerCase().endsWith(".jpeg") ||
                name.toLowerCase().endsWith(".gif"));

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // 使用 UUID 生成新的文件名
                    String uuidFileName = UUID.randomUUID().toString() + getFileExtension(file.getName());
                    String repoPath = "images/" + uuidFileName;
                    String message = "Upload " + uuidFileName;

                    String fileContent = encodeFileToBase64(file.getAbsolutePath());
                    if (fileContent != null) {
                        // TODO 完成单个文件上传后修改
                        String imageUrl = uploadToGitHub(repoPath, fileContent, message);
                        if (imageUrl != null) {
                            // 成功上传后保存 URL
                            imageUrls.add(imageUrl);
                        }
                    }
                }
            }
        } else {
            System.out.println("No images found in the folder.");
        }
        return imageUrls;
    }

    // 将文件转换为 Base64 编码
    private static String encodeFileToBase64(String filePath) {
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            log.error("File read error: {}", e.getMessage());
            throw new BaseException(ResultEnum.FILE_READ_ERROR);
        }
    }

    // 上传文件到 GitHub
    private static String uploadToGitHub(String repoPath, String content, String commitMessage) {
        // 1. 创建 HttpClient 对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        return null;
    }

    // 获取文件扩展名
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        } else {
            return ""; // 无扩展名的情况
        }
    }
}
