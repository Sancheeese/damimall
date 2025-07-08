package com.example.common.constant;

import java.util.Queue;

public class ProductConstant {
    public static final String PRODUCT_INDEX = "damimall_product";
    public static final String PRODUCT_3LEVEL_CAT_CACHE_KEY = "product:3levelcat";
    public static final String PRODUCT_3LEVEL_EMPTY_VALUE = "";
    public static final String PRODUCT_3LEVEL_LOCK = "product:3levelcat:lock";

    public static final Integer PAGE_SIZE = 20;



    public enum AttrType{
        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");

        private int code;

        private String msg;

        AttrType(int code, String msg){
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

    public enum ProductStatus{
        PRODUCT_BUILD(0, "新建"),
        PRODUCT_UP(1,"上架"),
        PRODUCT_DOWN(2, "下架");

        private int code;

        private String msg;

        ProductStatus(int code, String msg) {
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
}
