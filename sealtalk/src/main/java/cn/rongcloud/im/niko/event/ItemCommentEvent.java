package cn.rongcloud.im.niko.event;


import cn.rongcloud.im.niko.model.niko.CommentBean;

public class ItemCommentEvent {
    private CommentBean commentBean;

    public CommentBean getCommentBean() {
        return commentBean;
    }

    public ItemCommentEvent(CommentBean commentBean) {
        this.commentBean = commentBean;
    }
}
