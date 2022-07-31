package com.xcf.community.utils;

/**
 * @author Joe
 * @ClassName StatusCode.java
 * @Description
 * @createTime 2022年06月04日 13:54:00
 */
public enum StatusCode {
    SUCCESS("success",200),ERROR("error",500);
    private String msg;
    private Integer code;

    StatusCode(String msg,Integer code){
        this.msg = msg;
        this.code = code;
    }
    StatusCode(Integer code){
        this.code = code;
    }
    StatusCode(String msg){
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
}
