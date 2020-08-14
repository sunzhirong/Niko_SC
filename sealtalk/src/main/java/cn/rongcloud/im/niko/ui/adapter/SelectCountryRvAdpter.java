package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.model.AreaBean;
import cn.rongcloud.im.niko.ui.adapter.item.ItemSelectCity;

public class SelectCountryRvAdpter extends BaseRvAdapter<AreaBean>{
    public SelectCountryRvAdpter(Context context, List<AreaBean> datas) {
        super(context, datas);
    }

    @Override
    protected View getItemView(int viewType) {
        return new ItemSelectCity(mContext);
    }

    @Override
    protected void bindData(RecyclerView.ViewHolder holder, int position) {
        ((ItemSelectCity) holder.itemView).bindData(mDatas.get(position));
    }
}
