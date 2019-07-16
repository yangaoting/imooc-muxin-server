package com.imooc.service;

import com.imooc.pojo.MyFriends;

import java.util.List;

public interface MyFriendsService {
    List<MyFriends> queryMyFriends(String userId);
}
