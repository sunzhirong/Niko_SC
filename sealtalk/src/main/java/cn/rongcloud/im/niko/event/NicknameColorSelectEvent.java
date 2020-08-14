package cn.rongcloud.im.niko.event;

public class NicknameColorSelectEvent {
    public boolean selected;
    public int position;

    public NicknameColorSelectEvent(boolean selected, int position) {
        this.selected = selected;
        this.position = position;
    }
}
