package cn.rongcloud.im.niko.ui.adapter.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.SelectMyLikeEvent;
import cn.rongcloud.im.niko.model.niko.MyLikeBean;
import cn.rongcloud.im.niko.ui.adapter.BaseItemView;
import cn.rongcloud.im.niko.utils.BirthdayToAgeUtil;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.eventbus.EventBus;

public class ItemMyLiked extends BaseItemView {
    @BindView(R.id.iv_left)
    ImageView mIvLeft;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_date)
    TextView mTvDate;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.iv_right)
    ImageView mIvRight;
    @BindView(R.id.iv_select)
    ImageView mIvSelect;
    private MyLikeBean bean;

    public ItemMyLiked(Context context) {
        super(context);
    }

    public ItemMyLiked(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_my_liked;
    }

    public void bindData(MyLikeBean myLikeBean) {
        this.bean = myLikeBean;
        GlideImageLoaderUtil.loadCircleImage(mContext,mIvLeft,bean.getUserHead().getUserIcon());
        mTvName.setText(bean.getUserHead().getName());
        mTvDate.setText(BirthdayToAgeUtil.scFormatYearMonth(bean.getUtc()));
        mTvContent.setText(bean.getMsg());
        mTvTitle.setText(bean.getTitle());
        GlideImageLoaderUtil.loadImage(mContext,mIvRight,bean.getMdGuid());
    }

    @OnClick(R.id.ll_container)
    public void onViewClicked() {
        mIvSelect.setSelected(!mIvSelect.isSelected());
        EventBus.getDefault().post(new SelectMyLikeEvent(bean));
    }
}
