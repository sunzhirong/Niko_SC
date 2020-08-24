package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.view.SealTitleBar;
import cn.rongcloud.im.niko.ui.widget.TitleBar;

public abstract class TitleBaseActivity extends BaseActivity {
    private ViewFlipper contentContainer;
    private SealTitleBar titleBar;
    protected TextView tvCancel;
    protected TextView tvManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        titleBar = findViewById(R.id.title_bar);
        tvCancel = findViewById(R.id.tv_cancel);
        tvManager = findViewById(R.id.tv_manager);
        contentContainer = findViewById(R.id.layout_container);
        setTitleBarType(SealTitleBar.Type.NORMAL);
        titleBar.setOnBtnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setContentView(View view) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        contentContainer.addView(view, lp);
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    public SealTitleBar getTitleBar() {
        return titleBar;
    }

    public void setTitleBarType(SealTitleBar.Type type) {
        titleBar.setType(type);
    }


    @Override
    public void finish() {
        super.finish();
        hideInputKeyboard();
    }
}
