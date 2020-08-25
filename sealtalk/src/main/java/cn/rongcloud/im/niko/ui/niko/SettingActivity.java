package cn.rongcloud.im.niko.ui.niko;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.LogoutEvent;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.sp.SPUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.activity.ContactCompanyActivity;
import cn.rongcloud.im.niko.ui.activity.LoginActivity;
import cn.rongcloud.im.niko.ui.activity.ModifyPwdActivity;
import cn.rongcloud.im.niko.ui.activity.SettingNotificationActivity;
import cn.rongcloud.im.niko.ui.activity.SettingPersonInfoActivity;
import cn.rongcloud.im.niko.ui.activity.SettingPwdActivity;
import cn.rongcloud.im.niko.ui.activity.VipActivity;
import cn.rongcloud.im.niko.ui.dialog.ClearCacheDialog;
import cn.rongcloud.im.niko.ui.dialog.CommonDialog;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getHasSetPasswordResult().observe(this,result->{
            if(result.RsCode==3){
                SPUtils.setHasPassword(mContext,result.getRsData());
            }
        });
        mUserInfoViewModel.hasSetPassword();

        mUserInfoViewModel.getLogoutResult().observe(this,resource->{
            if (resource.status == Status.SUCCESS) {
                dismissLoadingDialog(new Runnable() {
                    @Override
                    public void run() {
                        //退出到login
                        readyGo(LoginActivity.class);
                        EventBus.getDefault().post(new LogoutEvent());
                        finish();
                    }
                });

            } else if (resource.status == Status.LOADING) {
                showLoadingDialog("");
            } else {
                dismissLoadingDialog(new Runnable() {
                    @Override
                    public void run() {
                        showToast(resource.message);
                    }
                });
            }
        });
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
                if (!SPUtils.getHasPassword(mContext)) {
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
