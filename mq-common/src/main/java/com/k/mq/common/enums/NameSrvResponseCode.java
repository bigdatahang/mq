package com.k.mq.common.enums;

public enum NameSrvResponseCode {
    ERROR_USER_OR_PASSWORD(1001, "账号验证异常");

    int code;
    String desc;

    NameSrvResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
