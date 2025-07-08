package com.example.damimall.member.utils;

public class UserInfoUtils {
    private static ThreadLocal<UserInfoVo> userInfo = new ThreadLocal<>();

    public static void setUser(UserInfoVo user){
        userInfo.set(user);
    }

    public static UserInfoVo getUser(){
        return userInfo.get();
    }

    public static void delUser(){
        userInfo.remove();
    }
}
