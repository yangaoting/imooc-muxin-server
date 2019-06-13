package com.imooc.mapper;

import com.imooc.pojo.FriendsRequest;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FriendsRequestMapper extends MyMapper<FriendsRequest> {
    /**
     * 查询添加好友请求列表
     * @param acceptUserId
     * @return
     */
    public List<FriendsRequest> queryFriendRequestList(@Param("acceptUserId") String acceptUserId);
}