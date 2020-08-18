package cn.rongcloud.im.niko.ui.adapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.SelectAtEvent;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.adapter.BaseItemView;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.eventbus.EventBus;
import io.rong.imkit.widget.AsyncImageView;

public class ItemFollow extends BaseItemView {
    @BindView(R.id.tv_select)
    TextView mTvSelect;
    @BindView(R.id.rc_left)
    AsyncImageView mRcLeft;
    @BindView(R.id.tv_name)
    TextView mTvName;
    private FollowBean bean;

    public ItemFollow(Context context) {
        super(context);
    }

    public ItemFollow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_follow;
    }


    public void bindData(FollowBean bean) {
        this.bean = bean;
        mTvName.setText(bean.getName());
        mTvName.setTextColor(ProfileUtils.getNameColor(bean.getNameColor()));
        GlideImageLoaderUtil.loadCircleImage(mContext,mRcLeft,bean.getUserIcon());
        mTvSelect.setSelected(bean.isSelect());
    }

    @OnClick(R.id.ll_container)
    public void onViewClicked() {
        bean.setSelect(!bean.isSelect());
        mTvSelect.setSelected(bean.isSelect());
        EventBus.getDefault().post(new SelectAtEvent(bean));
    }
}
