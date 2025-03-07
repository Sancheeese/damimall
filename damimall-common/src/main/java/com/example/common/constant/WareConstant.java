package com.example.common.constant;

public class WareConstant {
    public enum AttrType{
        ORDER_BUILD(0, "采购单新建"),
        ORDER_ALLOCATED(1, "采购单已分配"),
        ORDER_RECEIVE(2, "采购单已领取"),
        ORDER_FINISH(3, "采购单已完成"),
        ORDER_ERROR(4, "采购单有异常"),

        REQUIRE_BUILD(0, "采购需求新建"),
        REQUIRE_ALLOCATED(1, "采购需求已分配"),
        REQUIRE_RECEIVE(2, "采购需求正在进行"),
        REQUIRE_FINISH(3, "采购需求已完成"),
        REQUIRE_ERROR(4, "采购需求有异常");


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
}
