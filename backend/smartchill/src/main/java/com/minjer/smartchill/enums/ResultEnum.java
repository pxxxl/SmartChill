package com.minjer.smartchill.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultEnum {
    SUCCESS(200,"操作成功"),
    ERROR(500,"系统异常，请联系管理员"),
    AUTH_ERROR(401, "Token过期或不存在"),
    PAGE_NOT_FOUND(404, "请求资源不存在"),

    // 业务异常
    ACCOUNT_NOT_EXIST(1001, "账号不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    ACCOUNT_EXIST(1003, "账号已存在"),
    UPLOAD_FAILED(1004, "文件上传失败"),
    ;



    private final Integer code;
    private final String msg;
}
