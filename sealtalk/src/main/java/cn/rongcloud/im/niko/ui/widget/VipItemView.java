package cn.rongcloud.im.niko.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import cn.rongcloud.im.niko.R;

public class VipItemView extends LinearLayout {

    private ImageView mIvLeft;
    private ImageView mIvRight;
    private TextView mTvContent;

    public VipItemView(Context context) {
        super(context);
        init(null);
    }

    public VipItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public VipItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View view = View.inflate(getContext(), R.layout.item_vip, this);
        mIvLeft = (ImageView) view.findViewById(R.id.iv_left);
        mIvRight = (ImageView) view.findViewById(R.id.iv_right);
        mTvContent = (TextView) view.findViewById(R.id.tv_content);

        setClickable(true);

        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.VipItemView);
            if (array.hasValue(R.styleable.VipItemView_vip_left_src)) {
                mIvLeft.setBackgroundResource(array.getResourceId(R.styleable.VipItemView_vip_left_src,R.drawable.img_vip_bottom_1));
            }
            if (array.hasValue(R.styleable.VipItemView_vip_content)) {
                mTvContent.setText(array.getString(R.styleable.VipItemView_vip_content));
            }

            if (array.hasValue(R.styleable.VipItemView_vip_select)) {
                mIvRight.setSelected((array.getBoolean(R.styleable.VipItemView_vip_select,false)));
            }
            array.recycle();
        }

//        this.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mIvRight.setSelected(!mIvRight.isSelected());
//            }
//        });
    }

}
