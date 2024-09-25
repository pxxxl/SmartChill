package com.minjer.smartchill.exception;

import com.minjer.smartchill.enums.ResultEnum;
import lombok.Data;

@Data
public class BaseException extends RuntimeException {

    private ResultEnum resultEnum;

    public BaseException() {
    }

    public BaseException(ResultEnum resultEnum) {
        super(resultEnum.getMsg());
        this.resultEnum = resultEnum;
    }
}
