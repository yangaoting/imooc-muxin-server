package com.imooc.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringContextUtils.applicationContext == null){
            SpringContextUtils.applicationContext = applicationContext;
        }
    }

    //获取上下文
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }

    //获取Bean实例，通过名称
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //获取Bean实例，通过类型
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //获取Bean实例，通过名称和类型
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name,clazz);
    }
}
