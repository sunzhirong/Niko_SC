package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.ui.adapter.item.ItemMyLiked;

public class MyLikedRvAdapter extends BaseRvAdapter<MyLikeBean> {
    public MyLikedRvAdapter(Context context, List<MyLikeBean> datas) {
        super(context, datas);
    }

    @Override
    protected View getItemView(int viewType) {
        return new ItemMyLiked(mContext);
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder holder, int position) {
        ((ItemMyLiked) holder.itemView).bindData(mDatas.get(position));
    }
}
