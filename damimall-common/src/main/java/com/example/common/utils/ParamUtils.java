package com.example.common.utils;

import java.util.Map;

public class ParamUtils {
    public static boolean isNullOrZero(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value == null || "0".equals(value);
    }

    public static boolean isNullOrZero(String key){
        return key == null || "0".equals(key);
    }

    public static boolean isNullOrZero(Long key){
        return key == null || key.equals(0L);
    }

    public static boolean isNullOrEmpty(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value == null || "".equals(value);
    }

    public static boolean isNullOrEmpty(String key){
        return key == null || "".equals(key);
    }

    public static boolean isAvailable(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value != null && !"".equals(value) && !"0".equals(value);
    }

    public static boolean isAvailable(String key){
        return key != null && !"".equals(key) && !"0".equals(key);
    }
}
