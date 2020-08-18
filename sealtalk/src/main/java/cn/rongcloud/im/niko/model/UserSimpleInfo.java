package cn.rongcloud.im.niko.model;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class UserSimpleInfo{
    private String id;
    @ColumnInfo(name = "portrait_uri")
    private String portraitUri;
    @SerializedName(value = "name" ,alternate = {"nickname"})
    private String name;

    @ColumnInfo(name = "name_color")
    private String nameColor;

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
