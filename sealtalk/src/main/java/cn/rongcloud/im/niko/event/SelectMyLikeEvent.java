package cn.rongcloud.im.niko.event;


import cn.rongcloud.im.niko.model.niko.MyLikeBean;

public class SelectMyLikeEvent {
    public MyLikeBean bean ;
    public SelectMyLikeEvent(MyLikeBean bean) {
        this.bean = bean;
    }
}
