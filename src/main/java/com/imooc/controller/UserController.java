package com.imooc.controller;


import com.imooc.ImoocMuxinNettyApplication;
import com.imooc.enums.OperateFriendRequestTypeNum;
import com.imooc.enums.SearchFriendsStatusEnums;
import com.imooc.pojo.ChatMsg;
import com.imooc.pojo.FriendsRequest;
import com.imooc.pojo.MyFriends;
import com.imooc.pojo.User;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.ChatMsgService;
import com.imooc.service.FriendRequestService;
import com.imooc.service.MyFriendsService;
import com.imooc.service.UserService;
import com.imooc.utils.FastDFSClient;
import com.imooc.utils.FileUtils;
import com.imooc.utils.IMoocJSONResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.imooc.enums.OperateFriendRequestTypeNum.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private ChatMsgService chatMsgService;

    @Autowired
    private MyFriendsService myFriendsService;

    @Autowired
    private FastDFSClient fastDFSClient;

    @PostMapping("/registOrLogin")
    public IMoocJSONResult sayHello(@Valid @RequestBody User user, BindingResult bindingResult){
        //属性校验
        if(bindingResult.hasErrors()){
            return IMoocJSONResult.errorMsg(bindingResult.getFieldError().getDefaultMessage());
        }
        //存在则登录,不存在则注册
        boolean isExist = userService.queryUsernameIsExist(user.getUsername());
        User userResult = null;
        if(isExist){
            //登录
            userResult = userService.queryUserForLogin(user);
            if(userResult == null){
                return IMoocJSONResult.errorMsg("用户名货密码不正确");
            }
        }else{
            //注册
            user.setNickname(user.getUsername());
            user.setFaceImage("");
            user.setFaceImageBig("");

            userResult = userService.saveUser(user);
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult,userVO);
        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/uploadFaceBase64")
    public IMoocJSONResult uploadFaceBase64(@RequestBody UserBO userBO) throws Exception {

        //获取Base64字符串
        String base64Data = userBO.getFaceData();

        //定义临时目录
        String userFacePath = userBO.getUserId() + ".png";

        //Base64转换成文件
        FileUtils.base64ToFile(userFacePath,base64Data);
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);

        //上传到文件服务器
        String imgUrl = fastDFSClient.uploadBase64(faceFile);
        log.info("上传头像url:" + imgUrl);

        //缩略图url
        String thumpImgUrl = imgUrl.replaceAll("\\.", "_80x80.");

        //修改用户信息
        User user = new User();

        user.setId(userBO.getUserId());
        user.setFaceImage(thumpImgUrl);
        user.setFaceImageBig(imgUrl);

        user = userService.updateUserInfo(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);

        //结果返回
        return IMoocJSONResult.ok(userVO);
    }


    @PostMapping("/setNickname")
    public IMoocJSONResult setNickname(@RequestBody UserBO userBO) throws Exception {
        //修改用户信息
        User user = new User();

        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());

        user = userService.updateUserInfo(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);

        //结果返回
        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/searchUser")
    public IMoocJSONResult searchUser(String myUserId, String friendUsername) throws Exception {
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            log.error("请求参数为空");
            return IMoocJSONResult.errorMsg("请求参数为空[" + myUserId + ":" + friendUsername + "]");
        }

        SearchFriendsStatusEnums sfsEnums = userService.preconditionSearchFriends(myUserId,friendUsername);
        if(sfsEnums.status == SearchFriendsStatusEnums.SUCCESS.status){
            User user = userService.queryUserInfoByUsername(friendUsername);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);

            return IMoocJSONResult.ok(userVO);
        }else{
            log.error(myUserId + ":" + friendUsername + ">>>" +sfsEnums.desc);
            return IMoocJSONResult.errorMsg(sfsEnums.desc);
        }

    }

    @PostMapping("/addFriendRequest")
    public IMoocJSONResult addFriendRequest(String myUserId, String friendUsername) throws Exception {
        if(StringUtils.isBlank(myUserId) || StringUtils.isBlank(friendUsername)){
            log.error("请求参数为空");
            return IMoocJSONResult.errorMsg("请求参数为空[" + myUserId + ":" + friendUsername + "]");
        }

        SearchFriendsStatusEnums sfsEnums = userService.preconditionSearchFriends(myUserId,friendUsername);
        if(sfsEnums.status == SearchFriendsStatusEnums.SUCCESS.status){
           userService.sendFriendRequest(myUserId,friendUsername);
        }else{
            log.error(myUserId + ":" + friendUsername + ">>>" +sfsEnums.desc);
            return IMoocJSONResult.errorMsg(sfsEnums.desc);
        }

        return IMoocJSONResult.ok();
    }

    @PostMapping("/queryFriendRequests")
    public IMoocJSONResult queryFriendRequests(String userId) throws Exception {
        if(StringUtils.isBlank(userId)){
            log.error("请求参数为空");
            return IMoocJSONResult.errorMsg("请求参数为空[" + userId + "]");
        }
        List<FriendsRequest> friendsRequests = friendRequestService.queryFriendRequestList(userId);
        return IMoocJSONResult.ok(friendsRequests);
    }

    @PostMapping("/operFriendRequest")
    public IMoocJSONResult queryFriendRequests(String acceptUserId,String sendUserId,Integer operType) throws Exception {
        if(StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId)){
            log.error("请求参数为空[acceptUserId:\" + acceptUserId + \",sendUserId:\" + sendUserId+\"]");
            return IMoocJSONResult.errorMsg("请求参数为空[acceptUserId:" + acceptUserId + ",sendUserId:" + sendUserId+"]");
        }
        OperateFriendRequestTypeNum operTypeEnum = getInstance(operType);
        if(operTypeEnum == null){
            log.error("不支持该操作类型operType=" + operType);
            return IMoocJSONResult.errorMsg("不支持该操作类型operType=" + operType);
        }

        if(operTypeEnum.type == IGNORE.type){
            //删除请求记录
            friendRequestService.deleteFriendRequest(sendUserId,acceptUserId);
            log.info("忽略好友请求事务完成，无异常");
        }else if(operTypeEnum.type == PASS.type){
            //添加好友关系
            friendRequestService.passFriendRequest(sendUserId,acceptUserId);
            log.info("通过好友请求成功");

            //更新通讯录
            List<MyFriends> myFriendsList = myFriendsService.queryMyFriends(acceptUserId);
            return IMoocJSONResult.ok(myFriendsList);
        }
        return IMoocJSONResult.ok();
    }

    @PostMapping("/myFriends")
    public IMoocJSONResult myFriends(String userId) throws Exception {
        if(StringUtils.isBlank(userId)){
            log.error("请求参数为空[userId]");
            return IMoocJSONResult.errorMsg("请求参数为空[userId:" + userId + "]");
        }

        //查询好友列表
        List<MyFriends> myFriendsList = myFriendsService.queryMyFriends(userId);
        if(myFriendsList != null && myFriendsList.size() > 0){
            log.info("获取好友：userId" + userId + ",好友人数：" + myFriendsList.size() );
        }

        return IMoocJSONResult.ok(myFriendsList);
    }

    /**
     * 获取未读消息
     * @param acceptUserId
     * @return
     * @throws Exception
     */
    @PostMapping("/getUnReadMsgList")
    public IMoocJSONResult getUnReadMsgList(String acceptUserId) throws Exception {
        if(StringUtils.isBlank(acceptUserId)){
            log.error("请求参数为空[userId]");
            return IMoocJSONResult.errorMsg("请求参数为空[userId:" + acceptUserId + "]");
        }

        List<ChatMsg> unReadMsgList = chatMsgService.getUnReadMsgList(acceptUserId);
        log.info("acceptUserId:" + acceptUserId +",未读消息count:" + unReadMsgList.size());

        return IMoocJSONResult.ok(unReadMsgList);
    }
}
