package com.example.common.exception;

import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;

import java.util.Queue;

/**
 * 10:通用
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 * 15:用户
 * 21:库存
 */

public enum BizCodeEnum {
    SMS_CODE_EXCEPTION(10002, "验证码频率太高"),
    TOO_MANY_REQUEST(10003, "请求数量过多"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "用户存在异常"),
    PHONE_EXIST_EXCEPTION(15002, "电话号码存在异常"),
    LOGIN_PASSWORD_EXCEPTION(15003, "登录密码错误"),
    LOGIN_ACCOUNT_NOT_EXIST_EXCEPTION(15004, "用户不存在"),
    SOCIAL_LOGIN_MISSING_TOKEN_EXCEPTION(15005, "token不存在"),
    NO_STOCK_EXCEPTION(21000, "库存不足");


    private int code;

    private String msg;

    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
