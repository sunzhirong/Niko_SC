package cn.rongcloud.im.niko.ui.adapter.item;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.ItemCommentEvent;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.ui.adapter.BaseItemView;
import cn.rongcloud.im.niko.utils.BirthdayToAgeUtil;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import io.rong.eventbus.EventBus;

public class ItemComment extends BaseItemView {

    @BindView(R.id.iv_left)
    AppCompatImageView mIvLeft;
    @BindView(R.id.tv_content)
    AppCompatTextView mTvContent;
    @BindView(R.id.iv_right)
    AppCompatImageView mIvRight;
    private CommentBean commentBean;


    public ItemComment(Context context) {
        super(context);
    }

    public ItemComment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_comment;
    }

    @Override
    protected void initView() {

    }

    public void bindData(CommentBean commentBean) {
        this.commentBean = commentBean;
        GlideImageLoaderUtil.loadCircleImage(mContext,mIvLeft,commentBean.getUserHead().getUserIcon());
        GlideImageLoaderUtil.loadRoundImg(mContext,mIvRight,commentBean.getMdGuid(),10);


        String type = "",msg = commentBean.getMsg(),date= "";
//        if(!TextUtils.isEmpty(date)&&date.length()>10){
//            date = date.substring(5,10);
//        }
        date = BirthdayToAgeUtil.formatTimeMillis(commentBean.getUtc());
        String name = commentBean.getUserHead().getName();
        if(commentBean.getTUID() == 0 && commentBean.getAtUID() == 0){
            //别人评论你
            type = name+"评论了你的动态：";
        }else if(commentBean.getTUID()>0){
            //别人回复你
            type = name+"回复了你的评论：";
        }else if(commentBean.getAtUID() > 0){
            //别人@你
            type = name+"在评论中@了你";
            Spanned strA = Html.fromHtml("<font color=#65666C>" + type + "</font>");
            mTvContent.setText(strA);
            return;
        }


        Spanned strA = Html.fromHtml("<font color=#65666C>" + type +
                "</font>" + "<font color=#0A0A0B>" + msg + "<font color=#65666C>"
                + "  "+date+"</font>" );
        mTvContent.setText(strA);


    }

    @OnClick(R.id.ll_container)
    public void onViewClicked() {
        EventBus.getDefault().post(new ItemCommentEvent(commentBean));
    }


}
