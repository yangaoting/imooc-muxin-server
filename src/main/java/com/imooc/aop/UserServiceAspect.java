package com.imooc.aop;

import com.imooc.pojo.User;
import com.imooc.utils.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
public class UserServiceAspect {

    @Pointcut("execution(* com.imooc.service.UserService+.*(com.imooc.pojo.User,..))")
    public void passwordPoint(){
    }

    @Before(value = "passwordPoint()")
    public void beforeQueryLogin(JoinPoint joinPoint){
        log.info("aopzhixing..........");
        //获取方法所有参数
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        //参数所在位置
//        int index = ArrayUtils.indexOf(args, new User());
//        if(index == -1)
//            return;
        //取得user对象
        User user = (User) args[0];
        //密码不为空，查询数据之前进行md5加密
        if(StringUtils.isNotBlank(user.getPassword())){
            try {
                user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            } catch (Exception e) {
                log.error("用户密码进行md5加密失败");
            }
        }
    }
}
