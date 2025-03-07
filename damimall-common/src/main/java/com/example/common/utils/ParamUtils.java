package com.example.common.utils;

import java.util.Map;

public class ParamUtils {
    public static boolean isNullOrZero(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value == null || "0".equals(value);
    }

    public static boolean isNullOrEmpty(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value == null || "".equals(value);
    }

    public static boolean isAvailable(Map<String, Object> params, String key){
        String value = (String) params.get(key);
        return value != null && !"".equals(value) && !"0".equals(value);
    }
}
