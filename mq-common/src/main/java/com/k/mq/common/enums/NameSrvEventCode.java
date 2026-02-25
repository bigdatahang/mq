package com.k.mq.common.enums;

public enum NameSrvEventCode {
    REGISTRY(1, "注册事件"),
    UN_REGISTRY(2, "下线事件"),
    HEARTBEAT(3, "心跳事件");

    int code;
    String desc;

    NameSrvEventCode(int code, String desc) {
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
