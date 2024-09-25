package com.minjer.smartchill.entity.result;

import com.minjer.smartchill.enums.ResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    private Integer code;
    private String message;
    private Object data;

    public Result(int code) {
        this.code = code;
        this.message = "";
        this.data = null;
    }

    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMsg();
        this.data = null;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * 成功返回结果
     * @return Result
     */
    public static Result success() {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), null);
    }

    /**
     * 成功返回结果
     * @param data 响应数据
     * @return Result
     */
    public static Result success(Object data) {
        return new Result(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), data);
    }

    /**
     * 返回错误信息
     * @return
     */
    public static Result error() {
        return new Result(ResultEnum.ERROR.getCode(), ResultEnum.ERROR.getMsg(), null);
    }

    /**
     * 返回自定义错误信息
     * @param resultEnum 错误信息
     * @return
     */
    public static Result error(ResultEnum resultEnum) {
        return new Result(resultEnum.getCode(), resultEnum.getMsg(), null);
    }

}
