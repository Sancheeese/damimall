package com.example.damimall.product.utils;

public interface ILock {
    boolean tryLock(Long expireSec, String value);

    void unLock(String value);
}