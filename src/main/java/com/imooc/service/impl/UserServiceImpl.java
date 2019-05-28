package com.imooc.service.impl;

import com.imooc.mapper.UserMapper;
import com.imooc.pojo.User;
import com.imooc.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import static tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        User user = new User();
        user.setUsername(username);

        User userResult = userMapper.selectOne(user);

        return userResult != null;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User queryUserForLogin(User user) {
        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",user.getUsername());
        criteria.andEqualTo("password",user.getPassword());

        User userResult = userMapper.selectOneByExample(userExample);

        return userResult;
    }

    @Override
    public User saveUser(User user) {
        //生成二维码
        user.setQrcode("");

        user.setId(sid.nextShort());
        userMapper.insert(user);

        return user;
    }
}
