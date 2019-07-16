package com.imooc.mapper;

import com.imooc.pojo.ChatMsg;
import com.imooc.utils.MyMapper;

import java.util.List;

public interface ChatMsgMapper extends MyMapper<ChatMsg> {
    /**
     * 批量签收
     * @param msgIdList
     */
    void batchUpdateSigned(List<String> msgIdList);
}