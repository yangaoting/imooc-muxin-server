package com.imooc.netty.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChatMsg implements Serializable {

    //发送人
    private String sendId;
    //接收人
    private String receiverId;
    //消息内容
    private String msg;
    //消息ID,用于消息签收
    private String msgId;

}
