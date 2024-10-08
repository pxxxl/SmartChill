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
    UNPROCESABLE_ENTITY(422, "参数校验失败"),

    // 业务异常
    ACCOUNT_NOT_EXIST(1001, "账号不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    ACCOUNT_EXIST(1003, "账号已存在"),
    UPLOAD_FAILED(1004, "文件上传失败"),
    DRINK_NOT_EXIST(1005, "饮品不存在"),
    PRICE_ERROR(1006, "价格不符合要求，请控制在0.1-99.9之间"),
    DRINK_UNSOLD(1007, "饮品未销售完毕，不允许补货"),
    OUT_OF_STOCK(1008, "库存不足"),

    FILE_READ_ERROR(2001, "文件读取失败"), ;



    private final Integer code;
    private final String msg;
}
