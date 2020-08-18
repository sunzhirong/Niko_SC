package cn.rongcloud.im.niko.event;


import cn.rongcloud.im.niko.model.niko.FollowBean;

public class SelectAtEvent {
    public FollowBean bean ;
    public SelectAtEvent(FollowBean bean) {
        this.bean = bean;
    }
}
