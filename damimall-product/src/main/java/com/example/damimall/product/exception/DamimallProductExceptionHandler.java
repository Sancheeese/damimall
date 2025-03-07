package com.example.damimall.product.exception;

import com.example.common.utils.R;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class DamimallProductExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R validException(MethodArgumentNotValidException exception){
        List<FieldError> fieldErrors = exception.getFieldErrors();
        Map<String, String> map = new HashMap<>();
        for (FieldError fe : fieldErrors){
            map.put(fe.getField(), fe.getDefaultMessage());
        }
        return R.error(400, "商品操作数据校验失败").put("detail", map);
    }
}
