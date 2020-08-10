package cn.rongcloud.im.niko.ui.adapter.models;

public class SearchTitleModel extends SearchModel<Integer> {
    public SearchTitleModel(Integer bean, int type, int priority) {
        super(bean, type);
        this.priority = priority;
    }
}
