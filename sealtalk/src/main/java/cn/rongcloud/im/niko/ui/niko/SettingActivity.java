package cn.rongcloud.im.niko.ui.niko;

import android.os.Bundle;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.activity.ContactCompanyActivity;
import cn.rongcloud.im.niko.ui.activity.ModifyPwdActivity;
import cn.rongcloud.im.niko.ui.activity.SettingNotificationActivity;
import cn.rongcloud.im.niko.ui.activity.SettingPersonInfoActivity;
import cn.rongcloud.im.niko.ui.activity.SettingPwdActivity;
import cn.rongcloud.im.niko.ui.activity.VipActivity;
import cn.rongcloud.im.niko.ui.dialog.ClearCacheDialog;
import cn.rongcloud.im.niko.ui.dialog.CommonDialog;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.siv_info)
    SettingItemView mSivInfo;
    @BindView(R.id.siv_notification)
    SettingItemView mSivNotification;
    @BindView(R.id.siv_hobby)
    SettingItemView mSivHobby;
    @BindView(R.id.siv_contact)
    SettingItemView mSivContact;
    @BindView(R.id.siv_modify_pwd)
    SettingItemView mSivModifyPwd;
    @BindView(R.id.siv_company)
    SettingItemView mSivCompany;
    @BindView(R.id.siv_clear)
    SettingItemView mSivClear;
    @BindView(R.id.siv_logout)
    SettingItemView mSivLogout;

    private CommonDialog mLogoutDialog;
    private CommonDialog mClearCacheDialog;
    private UserInfoViewModel mUserInfoViewModel;
    

    protected int getLayoutId() {
        return R.layout.activity_setting;
    }


    @OnClick({R.id.siv_info, R.id.siv_notification, R.id.siv_hobby, R.id.siv_contact, R.id.siv_modify_pwd,
            R.id.siv_company, R.id.siv_clear, R.id.siv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_info:
                readyGo(SettingPersonInfoActivity.class);
                break;
            case R.id.siv_notification:
                readyGo(SettingNotificationActivity.class);
                break;
            case R.id.siv_hobby:

                break;
            case R.id.siv_contact:
                readyGo(VipActivity.class);
                break;
            case R.id.siv_modify_pwd:
                if (!ProfileUtils.hasSetPw) {
                    readyGo(SettingPwdActivity.class);
                } else {
                    readyGo(ModifyPwdActivity.class);
                }
                break;
            case R.id.siv_company:
                readyGo(ContactCompanyActivity.class);
                break;
            case R.id.siv_clear:
                showClearDialog();
                break;
            case R.id.siv_logout:
                logout();
                break;
        }
    }

    private void showClearDialog() {
        if(mClearCacheDialog==null) {
            mClearCacheDialog = new ClearCacheDialog.Builder()
                    .setTitleText(R.string.seal_set_account_dialog_clear_cache_title)
                    .setContentMessage(getString(R.string.seal_set_account_dialog_clear_cache_message))
                    .setButtonText(R.string.common_clear, R.string.common_cancel)
                    .build();
        }
        mClearCacheDialog.show(getSupportFragmentManager(), "clear_cache");
    }


    private void logout() {
        if(mLogoutDialog==null) {
            mLogoutDialog = new CommonDialog.Builder()
                    .setTitleText(R.string.dialog_logout_title)
                    .setContentMessage(getString(R.string.dialog_logout_content))
                    .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClick(View v, Bundle bundle) {
//                            mLoginViewModel.login("", "13305938755", "qq123456");
                            mUserInfoViewModel.logout();
                        }

                        @Override
                        public void onNegativeClick(View v, Bundle bundle) {
                        }
                    })
                    .build();
        }
        mLogoutDialog.show(getSupportFragmentManager(), "logout");
    }
}
