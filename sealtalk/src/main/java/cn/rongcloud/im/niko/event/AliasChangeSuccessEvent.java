package cn.rongcloud.im.niko.event;

/**
 * Created by xiaoxin on 2020/8/10
 * ClassDescription :别名修改完成事件
 */
public class AliasChangeSuccessEvent {
    private String alias;

    public AliasChangeSuccessEvent(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
