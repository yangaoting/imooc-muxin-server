package com.imooc.service.impl;

import com.imooc.enums.MsgActionEnum;
import com.imooc.mapper.FriendsRequestMapper;
import com.imooc.mapper.MyFriendsMapper;
import com.imooc.netty.pojo.DataContent;
import com.imooc.netty.pojo.UserChannelRel;
import com.imooc.pojo.FriendsRequest;
import com.imooc.pojo.MyFriends;
import com.imooc.service.FriendRequestService;
import com.imooc.utils.JsonUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Slf4j
public class FriendRequestServiceImpl implements FriendRequestService {

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FriendsRequest> queryFriendRequestList(String acceptUserId) {
        List<FriendsRequest> list = friendsRequestMapper.queryFriendRequestList(acceptUserId);
        assert list.size() == 1;
        log.info(JsonUtils.objectToJson(list));
        return list;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("sendUserId",sendUserId);
        criteria.andEqualTo("acceptUserId",acceptUserId);

        int i = friendsRequestMapper.deleteByExample(example);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void passFriendRequest(String sendUserId, String acceptUserId) {
        saveFriends(sendUserId,acceptUserId);
        saveFriends(acceptUserId,sendUserId);

        deleteFriendRequest(sendUserId,acceptUserId);

        Channel channel = UserChannelRel.get(sendUserId);
        if(channel != null){
            //使用微博socket推送到请求发起者，更新通讯录
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.objectToJson(dataContent)));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void saveFriends(String sendUserId, String acceptUserId){

        MyFriends myFriends = new MyFriends();

        myFriends.setId(Sid.next());
        myFriends.setMyUserId(sendUserId);
        myFriends.setMyFriendUserId(acceptUserId);

        myFriendsMapper.insert(myFriends);
    }
}
