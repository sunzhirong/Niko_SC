package cn.rongcloud.im.niko.ui.adapter.models;

public class VIPConfigBean {

    /**
     * Title : string
     * NameColor : string
     * ImgMdGuid : string
     */

    private String Title;
    private String NameColor;
    private String ImgMdGuid;
    private boolean select;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getNameColor() {
        return NameColor;
    }

    public void setNameColor(String NameColor) {
        this.NameColor = NameColor;
    }

    public String getImgMdGuid() {
        return ImgMdGuid;
    }

    public void setImgMdGuid(String ImgMdGuid) {
        this.ImgMdGuid = ImgMdGuid;
    }
}
