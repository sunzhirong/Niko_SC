package cn.rongcloud.im.niko.ui.adapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.SelectFriendEvent;
import cn.rongcloud.im.niko.model.niko.FriendBean;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.adapter.BaseItemView;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.eventbus.EventBus;
import io.rong.imkit.widget.AsyncImageView;

public class ItemFriend extends BaseItemView {
    @BindView(R.id.tv_select)
    TextView mTvSelect;
    @BindView(R.id.rc_left)
    AsyncImageView mRcLeft;
    @BindView(R.id.tv_name)
    TextView mTvName;
    private FriendBean bean;

    public ItemFriend(Context context) {
        super(context);
    }

    public ItemFriend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_friend;
    }

    public void bindData(FriendBean friendInfo) {
        this.bean = friendInfo;
        mTvName.setText(bean.getName());
        mTvName.setTextColor(ProfileUtils.getNameColor(bean.getNameColor()));
        GlideImageLoaderUtil.loadCircleImage(mContext,mRcLeft,bean.getUserIcon());
    }

    @OnClick(R.id.ll_container)
    public void onViewClicked() {
        mTvSelect.setSelected(!mTvSelect.isSelected());
        EventBus.getDefault().post(new SelectFriendEvent(bean));
    }
}
