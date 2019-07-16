package com.imooc.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "my_friends")
public class MyFriends {
    @Id
    private String id;

    @Column(name = "my_user_id")
    private String myUserId;

    @Column(name = "my_friend_user_id")
    private String myFriendUserId;

    @Transient
    private User myFriend;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return my_user_id
     */
    public String getMyUserId() {
        return myUserId;
    }

    /**
     * @param myUserId
     */
    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    /**
     * @return my_friend_user_id
     */
    public String getMyFriendUserId() {
        return myFriendUserId;
    }

    /**
     * @param myFriendUserId
     */
    public void setMyFriendUserId(String myFriendUserId) {
        this.myFriendUserId = myFriendUserId;
    }

    public User getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(User myFriend) {
        this.myFriend = myFriend;
    }
}