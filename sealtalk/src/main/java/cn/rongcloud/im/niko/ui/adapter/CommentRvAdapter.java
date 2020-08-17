package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.ui.adapter.item.ItemComment;

public class CommentRvAdapter extends BaseRvAdapter<CommentBean> {
    public CommentRvAdapter(Context context, List<CommentBean> datas) {
        super(context, datas);
    }

    @Override
    protected View getItemView(int viewType) {
        return new ItemComment(mContext);
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder holder, int position) {
        ((ItemComment) holder.itemView).bindData(mDatas.get(position));
    }
}
