package com.imooc.service;


import com.imooc.netty.pojo.ChatMsg;

import java.util.List;

public interface ChatMsgService {
    /**
     * 保存聊天消息到数据库
     * @param chatMsg
     * @return
     */
    public String save(ChatMsg chatMsg);

    /**
     * 修改消息的签收状态
     * @param msgIdList
     */
    public void updateMsgSigned(List<String> msgIdList);

    /**
     * 获取未读消息
     * @param acceptUserId
     * @return
     */
    public List<com.imooc.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
