package cn.rongcloud.im.niko.event;

import java.util.List;

import cn.rongcloud.im.niko.model.niko.FollowBean;


public class SelectCompleteEvent {
    public List<FollowBean> list ;
    public SelectCompleteEvent(List<FollowBean> list) {
        this.list = list;
    }
}
