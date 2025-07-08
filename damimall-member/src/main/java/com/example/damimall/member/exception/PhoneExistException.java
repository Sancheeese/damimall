package com.example.damimall.member.exception;

public class PhoneExistException extends RuntimeException{
    public PhoneExistException(){
        super("手机号存在");
    }
}
