package com.imooc.netty.pojo;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * 用户与channel关联
 */
public class UserChannelRel {

    private static HashMap<String, Channel> manager = new HashMap<>();

    public static void put(String sendId,Channel channel){
        manager.put(sendId,channel);
    }

    public static Channel get(String sendId){
        return manager.get(sendId);
    }

    public static void print(){
        manager.entrySet().forEach(entry -> {
            System.out.println("UserId:" + entry.getKey() + ",ChannelId:" + entry.getValue().id().asLongText());
        });
    }
}
