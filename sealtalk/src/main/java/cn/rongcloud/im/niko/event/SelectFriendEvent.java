package cn.rongcloud.im.niko.event;


import cn.rongcloud.im.niko.model.niko.FriendBean;

public class SelectFriendEvent {
    public FriendBean bean ;
    public SelectFriendEvent(FriendBean bean) {
        this.bean = bean;
    }
}
