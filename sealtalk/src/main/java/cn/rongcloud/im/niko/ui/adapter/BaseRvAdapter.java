package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import cn.rongcloud.im.niko.utils.log.SLog;

/**
 * Created by niko on 2018/10/30.
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    protected Context mContext;
    protected List<T> mDatas;
    private OnItemClickListener mListener;

    public BaseRvAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
    }

    public void setDatas(List<T> datas){
        if(datas!=null){
            mDatas = datas;
            notifyDataSetChanged();
        }

    }
    public void addDatas(List<T> datas){
        if(datas!=null&&datas.size()!=0) {
            int itemCount = getItemCount();
            mDatas.addAll(datas);
            notifyItemRangeChanged(itemCount,getItemCount());
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = getItemView(viewType);
        return new ViewHolder(itemView);
    }

    protected abstract View getItemView(int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindData(holder,position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }



    protected abstract void bindData(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        if(mDatas==null)return 0;
        SLog.e("BaseRvAdapter","mDatas.size() = "+mDatas.size());
        return mDatas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

}
