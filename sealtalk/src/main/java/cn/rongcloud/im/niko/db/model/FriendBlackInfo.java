package cn.rongcloud.im.niko.db.model;


import cn.rongcloud.im.niko.model.BlackListUser;

public class FriendBlackInfo {
    private BlackListUser user;

    public void setUser(BlackListUser user) {
        this.user = user;
    }

    public BlackListUser getUser() {
        return user;
    }

}