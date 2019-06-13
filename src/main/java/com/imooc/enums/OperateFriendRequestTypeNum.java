package com.imooc.enums;

import java.util.Arrays;
import java.util.function.Predicate;

public enum  OperateFriendRequestTypeNum {

    IGNORE(0,"忽略"),
    PASS(1,"通过")
    ;

    public final Integer type;
    public final String msg;

    OperateFriendRequestTypeNum(Integer type,String msg){
        this.type = type;
        this.msg = msg;
    }

    public static OperateFriendRequestTypeNum getInstance(final Integer type){
        OperateFriendRequestTypeNum[] nums = OperateFriendRequestTypeNum.values();
        OperateFriendRequestTypeNum operateFriendRequestTypeNum = Arrays.asList(nums).stream().filter(
                operNums -> { return operNums.type == type; }
        ).findFirst().orElse(null);

        return operateFriendRequestTypeNum;
    }
}
