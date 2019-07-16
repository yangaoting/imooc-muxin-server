package com.imooc.mapper;

import com.imooc.pojo.MyFriends;
import com.imooc.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MyFriendsMapper extends MyMapper<MyFriends> {

    public List<MyFriends> queryMyFriends(@Param("userId") String userId);
}