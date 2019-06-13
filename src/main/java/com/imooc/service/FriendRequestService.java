package com.imooc.service;

import com.imooc.pojo.FriendsRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendRequestService {

    public List<FriendsRequest> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId,String acceptUserId);

    /**
     * 好友请求通过
     * @param sendUserId
     * @param acceptUserId
     */
    public void passFriendRequest(String sendUserId,String acceptUserId);
}
