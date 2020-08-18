package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.ui.adapter.item.ItemFollow;

public class FollowRvAdapter extends BaseRvAdapter<FollowBean> {

    public FollowRvAdapter(Context context, List<FollowBean> datas) {
        super(context, datas);
    }

    @Override
    protected View getItemView(int viewType) {
        return new ItemFollow(mContext);
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder holder, int position) {
        ((ItemFollow) holder.itemView).bindData(mDatas.get(position));
    }
}
