package com.imooc.service.impl;

import com.imooc.mapper.MyFriendsMapper;
import com.imooc.pojo.MyFriends;
import com.imooc.service.MyFriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MyFriendsServiceImpl implements MyFriendsService {

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<MyFriends> queryMyFriends(String userId) {
        return myFriendsMapper.queryMyFriends(userId);
    }
}
