package com.minjer.smartchill.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class AccountUtil {
    public static String generateSalt() {
        // 随机生成一个8位的盐
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String hashPassword(String password, String salt) {
        try {
            // 创建SHA-256哈希对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将密码和盐拼接
            String saltedPassword = password + salt;

            // 计算哈希值
            byte[] hashedBytes = digest.digest(saltedPassword.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }
}
