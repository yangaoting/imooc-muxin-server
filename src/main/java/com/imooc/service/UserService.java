package com.imooc.service;

import com.imooc.enums.SearchFriendsStatusEnums;
import com.imooc.pojo.User;

public interface UserService {
    //判断用户是否存在
    public boolean queryUsernameIsExist(String username);

    //查询用户
    public User queryUserForLogin(User user);

    //注册用户
    public User saveUser(User user);

    //修改用户
    public User updateUserInfo(User user);

    //前置搜索用户
    SearchFriendsStatusEnums preconditionSearchFriends(String myUserId, String friendUsername);

    //查找用户信息
    User queryUserInfoByUsername(String friendUsername);

    //添加好友请求
    void sendFriendRequest(String myUserId, String friendUsername);
}
