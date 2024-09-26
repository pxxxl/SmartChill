package com.minjer.smartchill.exception;

import com.minjer.smartchill.entity.result.Result;
import com.minjer.smartchill.enums.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理可控异常  自定义异常
     *
     * @return Result
     */
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public Result exception(BaseException e) {
        log.error("BASE EXCEPTION");
        log.error("catch exception:{}", e.getMessage());
        return new Result(e.getResultEnum());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result exception(Exception e) {
        log.error("EXCEPTION");
        log.error("Exception class:{}", e.getClass().getName());
        log.error("catch exception:{}", e.getMessage());
        log.error("Detailed exception:{}", e.getCause());
        ;

        if (e.getClass().getName().contains("NoResourceFoundException")) {
            return new Result(ResultEnum.PAGE_NOT_FOUND);
        }

        return Result.error();
    }
}
