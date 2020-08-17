package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.ui.adapter.item.ItemFriend;

public class FriendRvAdapter extends BaseRvAdapter<FriendBean> {

    public FriendRvAdapter(Context context, List<FriendBean> datas) {
        super(context, datas);
    }

    @Override
    protected View getItemView(int viewType) {
        return new ItemFriend(mContext);
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder holder, int position) {
        ((ItemFriend) holder.itemView).bindData(mDatas.get(position));
    }
}
