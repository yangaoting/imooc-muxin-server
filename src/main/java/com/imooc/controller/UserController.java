package com.imooc.controller;

import com.imooc.pojo.User;
import com.imooc.pojo.vo.UserVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/u")
public class UserController {

    @Autowired
    private UserService userService;

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
}
