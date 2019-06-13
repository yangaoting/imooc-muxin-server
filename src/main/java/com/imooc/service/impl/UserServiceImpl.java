package com.imooc.service.impl;

import com.imooc.enums.SearchFriendsStatusEnums;
import com.imooc.mapper.FriendsRequestMapper;
import com.imooc.mapper.MyFriendsMapper;
import com.imooc.mapper.UserMapper;
import com.imooc.pojo.FriendsRequest;
import com.imooc.pojo.MyFriends;
import com.imooc.pojo.User;
import com.imooc.service.UserService;
import com.imooc.utils.FastDFSClient;
import com.imooc.utils.FileUtils;
import com.imooc.utils.QRCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static tk.mybatis.mapper.entity.Example.Criteria;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

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
    @Transactional(propagation = Propagation.SUPPORTS)
    public User saveUser(User user) {
        //二维码生成路径
        String qrCodeDirStr = "/qrcode/";
        File qrCodeDir = new File(qrCodeDirStr);
        if(!qrCodeDir.exists()){
            qrCodeDir.mkdirs();
        }
        String qrCodePath = qrCodeDirStr + user.getUsername() + ".png";
        String content = "muxin_qrcode:" + user.getUsername();
        //生成二维码
        qrCodeUtils.createQRCode(qrCodePath,content);
        //上传二维码到服务器
        MultipartFile multipartFile = FileUtils.fileToMultipart(qrCodePath);
        String qrCode = "";
        try {
            qrCode = fastDFSClient.uploadQRCode(multipartFile);
            log.info("生成二维码：" + qrCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setQrcode(qrCode);
        user.setId(sid.nextShort());
        userMapper.insert(user);

        return user;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User updateUserInfo(User user) {
        userMapper.updateByPrimaryKeySelective(user);

        return userMapper.selectByPrimaryKey(user.getId());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public SearchFriendsStatusEnums preconditionSearchFriends(String myUserId, String friendUsername) {
        User user = queryUserInfoByUsername(friendUsername);
        if(user == null){
            return SearchFriendsStatusEnums.NOT_EXISTS;
        }
        if(user.getId().equals(myUserId)){
            return SearchFriendsStatusEnums.IS_YOURSELF;
        }

        Example friendExample = new Example(MyFriends.class);
        Criteria criteria = friendExample.createCriteria();

        criteria.andEqualTo("myUserId",myUserId);
        criteria.andEqualTo("myFriendUserId",user.getId());

        MyFriends myFriends = myFriendsMapper.selectOneByExample(friendExample);

        if(myFriends != null){
            return SearchFriendsStatusEnums.ALREADY_FRIENDS;
        }

        return SearchFriendsStatusEnums.SUCCESS;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User queryUserInfoByUsername(String friendUsername) {
        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();

        criteria.andEqualTo("username",friendUsername);

        User userResult = userMapper.selectOneByExample(userExample);

        return userResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void sendFriendRequest(String myUserId, String friendUsername) {
        User friend = queryUserInfoByUsername(friendUsername);

        Example friendRequestExample = new Example(FriendsRequest.class);
        Criteria criteria = friendRequestExample.createCriteria();

        criteria.andEqualTo("sendUserId",myUserId);
        criteria.andEqualTo("acceptUserId",friend.getId());

        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(friendRequestExample);
        if(friendsRequest == null){
            FriendsRequest request = new FriendsRequest();

            request.setId(Sid.next());
            request.setSendUserId(myUserId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());

            friendsRequestMapper.insert(request);
        }

    }
}
