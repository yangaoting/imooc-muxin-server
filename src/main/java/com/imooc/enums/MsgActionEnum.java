package com.imooc.enums;

import java.util.Arrays;

/**
 * 消息枚举
 */
public enum  MsgActionEnum {
    CONNECT(1,"初始化连接"),
    CHAT(2,"聊天消息"),
    SIGNED(3,"消息签收"),
    KEEPALIVE(4,"客户端保持心跳"),
    PULL_FRIEND(5,"拉去好友列表"),
    ;
    public final Integer type;

    public final String content;

    MsgActionEnum(Integer type,String content){
        this.type = type;
        this.content = content;
    }

    public static MsgActionEnum getInstance(final Integer type){
        MsgActionEnum[] nums = MsgActionEnum.values();
        MsgActionEnum msgActionEnum = Arrays.asList(nums).stream().filter(
                operNums -> { return operNums.type == type; }
        ).findFirst().orElse(null);

        return msgActionEnum;
    }

    @Override
    public String toString() {
        return this.type  + "-->" + this.content;
    }
}
