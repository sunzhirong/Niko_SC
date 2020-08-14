package cn.rongcloud.im.niko.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.NicknameColorSelectEvent;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.adapter.BaseItemView;
import cn.rongcloud.im.niko.ui.adapter.models.VIPConfigBean;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.eventbus.EventBus;

public class SelectNicknameItemView extends BaseItemView {

    @BindView(R.id.iv_avatar)
    AppCompatImageView mIvAvatar;
    @BindView(R.id.tv_name)
    AppCompatTextView mTvName;
    @BindView(R.id.tv_color)
    AppCompatTextView mTvColor;
    @BindView(R.id.rl_left)
    RelativeLayout mRlLeft;
    @BindView(R.id.iv_select)
    AppCompatImageView mIvSelect;
    @BindView(R.id.rl_right)
    RelativeLayout mRlRight;
    @BindView(R.id.iv_right)
    AppCompatImageView mIvRight;
    @BindView(R.id.cv_container)
    CardView mCvContainer;
    private int position;

    public SelectNicknameItemView(Context context) {
        super(context);
    }

    public SelectNicknameItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_select_nickname_color;
    }

    @OnClick(R.id.cv_container)
    public void onViewClicked() {
//        mIvSelect.setSelected(!mIvSelect.isSelected());
        EventBus.getDefault().post(new NicknameColorSelectEvent(!mIvSelect.isSelected(),position));
    }

    public void bindData(VIPConfigBean bean, int position) {
        this.position = position;
        mRlLeft.setBackgroundColor(Color.parseColor("#"+bean.getNameColor()));
        GlideImageLoaderUtil.loadImage(mContext,mIvRight,bean.getImgMdGuid());
        mTvColor.setText(bean.getTitle());
        mTvName.setTextColor(Color.parseColor("#"+bean.getNameColor()));
        mTvName.setText(ProfileUtils.sProfileInfo.getHead().getName());
        GlideImageLoaderUtil.loadCircleImage(mContext,mIvAvatar, ProfileUtils.sProfileInfo.getHead().getUserIcon());
        mIvSelect.setSelected(bean.isSelect());
    }
}
