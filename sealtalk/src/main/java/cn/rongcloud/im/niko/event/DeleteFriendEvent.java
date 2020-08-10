package cn.rongcloud.im.niko.event;

public class DeleteFriendEvent {
    public String userId;
    public boolean result;

    public DeleteFriendEvent(String userId, boolean result) {
        this.userId = userId;
        this.result = result;
    }
}
