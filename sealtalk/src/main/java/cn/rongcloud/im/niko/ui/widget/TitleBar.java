package cn.rongcloud.im.niko.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.rongcloud.im.niko.R;


/**
 * Created by niko on 2019/5/17.
 */

public class TitleBar extends RelativeLayout {
    private ImageView mTitleBarIvBack;
    private ImageView mTitleBarIvRight;
    private TextView mTitleBarTvTitle;
    private TextView mTitleBarTvRight;
    private RelativeLayout mRlContainer;

    public TitleBar(Context context) {
        super(context);
        init(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);

        mTitleBarIvBack = (ImageView) findViewById(R.id.ivBack);
        mTitleBarIvRight = (ImageView) findViewById(R.id.ivRight);
        mTitleBarTvTitle = (TextView) findViewById(R.id.tvTitle);
        mTitleBarTvRight = (TextView) findViewById(R.id.tvRight);
        mRlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        View line = findViewById(R.id.line);


        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
            if (array.hasValue(R.styleable.TitleBar_title)) {
                mTitleBarTvTitle.setText(array.getString(R.styleable.TitleBar_title));
            }
            if (array.hasValue(R.styleable.TitleBar_right_title)) {
                mTitleBarTvRight.setText(array.getString(R.styleable.TitleBar_right_title));
            }
            if (array.hasValue(R.styleable.TitleBar_show_right_tv)) {
                boolean b = array.getBoolean(R.styleable.TitleBar_show_right_tv, false);
                mTitleBarTvRight.setVisibility(b?VISIBLE:GONE);
            }

            if(array.hasValue(R.styleable.TitleBar_show_line)){
                boolean b = array.getBoolean(R.styleable.TitleBar_show_line, true);
                line.setVisibility(b?VISIBLE:GONE);
            }

            if (array.hasValue(R.styleable.TitleBar_show_right_iv)) {
                boolean b = array.getBoolean(R.styleable.TitleBar_show_right_iv, false);
                mTitleBarIvRight.setVisibility(b?VISIBLE:GONE);
            }

            initBack();
            array.recycle();
        }
    }

    private void initBack() {
        this.mTitleBarIvBack.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                try {
                    Context context = TitleBar.this.getContext();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
                    if (context != null && context instanceof Activity) {
                        ((Activity) context).onBackPressed();
                    }

                } catch (Exception var4) {

                }
            }
        });
    }


    public void setTitle(int resId) {
        mTitleBarTvTitle.setText(resId);

    }

    public void setTitle(String title) {
        mTitleBarTvTitle.setText(title);

    }

    public void setTitleColor(int color){
        mTitleBarTvTitle.setTextColor(color);
    }

    public void setBackImageRes(int res) {
        mTitleBarIvBack.setImageResource(res);
    }

    public ImageView getBack() {
        return mTitleBarIvBack;
    }

    public void setBackOnClickListener(OnClickListener listener) {
        mTitleBarIvBack.setOnClickListener(listener);
    }

    public void setTitleBg(int color){
        mRlContainer.setBackgroundColor(color);
    }


    public ImageView getTitleBarIvRight(){
        return mTitleBarIvRight;
    }

    public TextView getTitleBarTvRight() {
        return mTitleBarTvRight;
    }

    public void setRightEnable(boolean b){
        mTitleBarTvRight.setEnabled(b);
    }
}
