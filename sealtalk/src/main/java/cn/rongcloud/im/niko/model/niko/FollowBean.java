package cn.rongcloud.im.niko.model.niko;

import java.util.Objects;

public class FollowBean {

    /**
     * Alias : string
     * IsStar : true
     * IsFriend : true
     * UID : 0
     * Name : string
     * NameColor : string
     * UserIcon : string
     * Gender : true
     */

    private String Alias;
    private boolean IsStar;
    private boolean IsFriend;
    private int UID;
    private String Name;
    private String NameColor;
    private String UserIcon;
    private boolean Gender;
    private boolean select;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String Alias) {
        this.Alias = Alias;
    }

    public boolean isIsStar() {
        return IsStar;
    }

    public void setIsStar(boolean IsStar) {
        this.IsStar = IsStar;
    }

    public boolean isIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(boolean IsFriend) {
        this.IsFriend = IsFriend;
    }

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNameColor() {
        return NameColor;
    }

    public void setNameColor(String NameColor) {
        this.NameColor = NameColor;
    }

    public String getUserIcon() {
        return UserIcon;
    }

    public void setUserIcon(String UserIcon) {
        this.UserIcon = UserIcon;
    }

    public boolean isGender() {
        return Gender;
    }

    public void setGender(boolean Gender) {
        this.Gender = Gender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowBean that = (FollowBean) o;
        return IsStar == that.IsStar &&
                IsFriend == that.IsFriend &&
                UID == that.UID &&
                Gender == that.Gender &&
                Objects.equals(Alias, that.Alias) &&
                Objects.equals(Name, that.Name) &&
                Objects.equals(NameColor, that.NameColor) &&
                Objects.equals(UserIcon, that.UserIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Alias, IsStar, IsFriend, UID, Name, NameColor, UserIcon, Gender);
    }
}
