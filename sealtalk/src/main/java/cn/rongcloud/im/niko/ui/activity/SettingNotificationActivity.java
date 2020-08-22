package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.viewmodel.NewMessageViewModel;

public class SettingNotificationActivity extends BaseActivity {
    @BindView(R.id.siv_no_disturb)
    SettingItemView mSivNoDisturb;
    @BindView(R.id.siv_show_detail)
    SettingItemView mSivShowDetail;
    @BindView(R.id.siv_sound)
    SettingItemView mSivSound;
    @BindView(R.id.siv_vibrate)
    SettingItemView mSivVibrate;
    private NewMessageViewModel newMessageViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_notification;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initViewModel();
    }

    private void initView() {
        //消息免打扰
        mSivNoDisturb.setSwitchCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setRemindStatus(isChecked);
            }
        });

        //显示通知详情
        mSivShowDetail.setSwitchCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNoticeDetail(isChecked);
            }
        });
    }

    /**
     * 设置新消息设置状态
     */
    public void setRemindStatus(boolean status) {
        if (newMessageViewModel != null) {
            newMessageViewModel.setRemindStatus(status);
        }
    }

    /**
     * 设置消息通知显示详情
     *
     * @param status 是否显示详情
     */
    public void setNoticeDetail(boolean status) {
        if (newMessageViewModel != null) {
            newMessageViewModel.setPushMsgDetailStatus(status);
        }
    }



    /**
     * 初始话ViModel
     */
    private void initViewModel() {
        newMessageViewModel = ViewModelProviders.of(this).get(NewMessageViewModel.class);

        // Remind 通知状态
        newMessageViewModel.getRemindStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean status) {
                mSivNoDisturb.setChecked(status);
            }
        });

        // 推送消息通知详情状态
        newMessageViewModel.getPushMsgDetailStatus().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> resource) {
                if (resource.status == Status.SUCCESS) {
                    Boolean isDetailStatus = resource.data;
                    if (isDetailStatus != null) {
                        mSivShowDetail.setCheckedImmediatelyWithOutEvent(isDetailStatus);
                    }
                } else if (resource.status == Status.ERROR) {
                    mSivShowDetail.setCheckedImmediatelyWithOutEvent(!mSivShowDetail.isChecked());
                    showToast(resource.message);
                }
            }
        });

    }
}
