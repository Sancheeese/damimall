package com.example.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperUtils {
    public static ObjectMapper objectMapper = new ObjectMapper();

//    处理jdk8的时间
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ObjectMapperUtils(){
    }

    public static <T> T readValue(String content, Class<T> valueType){
        T t = null;
        try {
            t = objectMapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("json转对象异常");
        }

        return t;
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef){
        T t = null;
        try {
            t = objectMapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("json转对象异常");
        }

        return t;
    }

    public static String writeValueAsString(Object value){
        String ret = null;
        try {
            ret = objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error("对象转json异常");
        }

        return ret;
    }


}
