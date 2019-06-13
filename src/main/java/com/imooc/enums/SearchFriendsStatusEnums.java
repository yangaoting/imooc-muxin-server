package com.imooc.enums;

import lombok.Data;

public enum SearchFriendsStatusEnums {
    SUCCESS(0,"成功"),
    NOT_EXISTS(1,"用户不存在"),
    IS_YOURSELF(2,"用户为自己"),
    ALREADY_FRIENDS(3,"已经是好友")
    ;

    public Integer status;
    public String desc;

    SearchFriendsStatusEnums(Integer status,String desc) {
        this.status = status;
        this.desc = desc;
    }
}
