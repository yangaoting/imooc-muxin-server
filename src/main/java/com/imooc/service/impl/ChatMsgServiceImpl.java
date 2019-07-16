package com.imooc.service.impl;

import com.imooc.enums.MsgSignFlagEnum;
import com.imooc.mapper.ChatMsgMapper;
import com.imooc.netty.pojo.ChatMsg;
import com.imooc.service.ChatMsgService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ChatMsgServiceImpl implements ChatMsgService {

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String save(ChatMsg chatMsg) {

        com.imooc.pojo.ChatMsg msgDB = new com.imooc.pojo.ChatMsg();

        msgDB.setId(Sid.nextShort());
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSendId());
        msgDB.setMsg(chatMsg.getMsg());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setCreateTime(new Date());

        chatMsgMapper.insert(msgDB);

        return msgDB.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMsgSigned(List<String> msgIdList) {
        chatMsgMapper.batchUpdateSigned(msgIdList);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<com.imooc.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
        Example chatExample = new Example(com.imooc.pojo.ChatMsg.class);
        Example.Criteria criteria = chatExample.createCriteria();
        criteria.andEqualTo("signFlag",0);
        criteria.andEqualTo("acceptUserId",acceptUserId);

        List<com.imooc.pojo.ChatMsg> chatMsgList = chatMsgMapper.selectByExample(chatExample);

        return chatMsgList;
    }
}
