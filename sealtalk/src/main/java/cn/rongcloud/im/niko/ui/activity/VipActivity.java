package cn.rongcloud.im.niko.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.adapter.models.VIPCheckBean;
import cn.rongcloud.im.niko.ui.widget.VipItemView;
import cn.rongcloud.im.niko.utils.BirthdayToAgeUtil;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;

public class VipActivity extends BaseActivity {

    @BindView(R.id.iv_avatar)
    AppCompatImageView mIvAvatar;
    @BindView(R.id.tv_name)
    AppCompatTextView mTvName;
    @BindView(R.id.iv_is_vip)
    AppCompatImageView mIvIsVip;
    @BindView(R.id.tv_use)
    AppCompatTextView mTvUse;
    @BindView(R.id.vip_1)
    VipItemView mVip1;
    @BindView(R.id.vip_2)
    VipItemView mVip2;
    @BindView(R.id.vip_3)
    VipItemView mVip3;
    @BindView(R.id.tv_code)
    TextView mTvCode;
    @BindView(R.id.tv_copy)
    TextView mTvCopy;
    private String mInviteCode;
    private UserInfoViewModel mUserInfoViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_vip;
    }

    private void initView() {
        initViewModel();
    }

    private void initViewModel() {
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getUserInfo().observe(this, new Observer<Resource<UserInfo>>() {
            @Override
            public void onChanged(Resource<UserInfo> resource) {
                if (resource.data != null) {
                    UserInfo info = resource.data;
                    mTvName.setTextColor(ProfileUtils.getNameColor(info.getNameColor()));
                    mTvName.setText(info.getName());
                    GlideImageLoaderUtil.loadCircleImage(mContext, mIvAvatar, info.getPortraitUri());
                }

            }
        });

        mUserInfoViewModel.getVipCheckResult().observe(this,result->{
            if (result.RsCode == 3) {
                VIPCheckBean rsData = result.RsData;
                mInviteCode = rsData.getInviteCode();
                mTvCode.setText("邀请码："+rsData.getInviteCode());

                if(!rsData.isStep1Way1()||!rsData.isStep1Way2()||!rsData.isStep2()){
                    //不是vip
                    mIvIsVip.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_vip_non_member));
                    mTvUse.setVisibility(View.GONE);
                }else {
                    mIvIsVip.setBackgroundDrawable(getResources().getDrawable(R.drawable.img_vip_member));
                    mTvUse.setVisibility(View.VISIBLE);
                    mVip1.setSelected(rsData.isStep1Way1());
                    mVip2.setSelected(rsData.isStep1Way2());
                    mVip3.setSelected(rsData.isStep2());
                }
            }
        });

        mUserInfoViewModel.vipCheck();
    }


    @OnClick({R.id.tv_use, R.id.tv_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_use:
                readyGo(SelectNickNameColorActivity.class);
                break;
            case R.id.tv_copy:
                if (!TextUtils.isEmpty(mInviteCode)) {
                    copy(mInviteCode);
                }
                break;
        }
    }

    private void copy(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            ToastUtils.showToast("复制成功");
        } catch (Exception e) {
        }
    }
}
