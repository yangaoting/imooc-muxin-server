package com.imooc.service;

import com.imooc.pojo.User;

public interface UserService {
    //判断用户是否存在
    public boolean queryUsernameIsExist(String username);

    //查询用户
    public User queryUserForLogin(User user);

    //注册用户
    public User saveUser(User user);
}
