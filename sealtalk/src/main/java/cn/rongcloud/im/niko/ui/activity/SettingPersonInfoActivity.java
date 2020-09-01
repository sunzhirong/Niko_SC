package cn.rongcloud.im.niko.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.ThreadManager;
import cn.rongcloud.im.niko.db.model.UserInfo;
import cn.rongcloud.im.niko.event.CitySelectEvent;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.dialog.SelectGenderBottomDialog;
import cn.rongcloud.im.niko.ui.dialog.SelectPictureBottomDialog;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.ui.widget.wheel.date.DatePickerDialogFragment;
import cn.rongcloud.im.niko.utils.BirthdayToAgeUtil;
import cn.rongcloud.im.niko.utils.ImageUtils;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.glideutils.GlideImageLoaderUtil;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.common.FileUtils;
import io.rong.eventbus.EventBus;

public class SettingPersonInfoActivity extends BaseActivity {

    public static final int TYPE_NICKNAME = 0;
    public static final int TYPE_SCHOOL = 1;
    public static final int TYPE_EMAIL = 2;

    @BindView(R.id.siv_img)
    SettingItemView mSivImg;
    @BindView(R.id.siv_nickname)
    SettingItemView mSivNickname;
    @BindView(R.id.siv_gender)
    SettingItemView mSivGender;
    @BindView(R.id.siv_city)
    SettingItemView mSivCity;
    @BindView(R.id.siv_own)
    SettingItemView mSivOwn;
    @BindView(R.id.siv_school)
    SettingItemView mSivSchool;
    @BindView(R.id.siv_age)
    SettingItemView mSivAge;
    private UserInfoViewModel mUserInfoViewModel;
    private ImageView mIvImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_person_info;
    }

    private void initView() {
        EventBus.getDefault().register(this);
        mSivImg.setImageVisibility(View.VISIBLE);
        mIvImage = mSivImg.getIvImage();
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getUserInfo().observe(this, new Observer<Resource<UserInfo>>() {
            @Override
            public void onChanged(Resource<UserInfo> resource) {
                if (resource.data != null) {
                    UserInfo info = resource.data;
                    mSivNickname.setValue(info.getName());
                    mSivNickname.setValueColor(ProfileUtils.getNameColor(info.getNameColor()));
                    GlideImageLoaderUtil.loadCircleImage(mContext, mIvImage,info.getPortraitUri());
                    mSivGender.setValue(info.isMan()?"男":"女");
                    mSivCity.setValue(info.getLocation());
                    mSivOwn.setValue(info.getBio());
                    mSivSchool.setValue(info.getSchool());
                    mSivAge.setValue(BirthdayToAgeUtil.birthdayToAge(info.getDob()));
                }

            }
        });

        mUserInfoViewModel.getUpdateProfile().observe(this,resource->{
            if (resource.status == Status.SUCCESS) {
                dismissLoadingDialog(new Runnable() {
                    @Override
                    public void run() {

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

        mUserInfoViewModel.getUploadResult().observe(this,resource->{
            if (resource.status == Status.SUCCESS) {
                dismissLoadingDialog(new Runnable() {
                    @Override
                    public void run() {
                        mUserInfoViewModel.updateProfile(3,"UserIcon",resource.data);
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

    @OnClick({R.id.siv_img, R.id.siv_nickname, R.id.siv_gender, R.id.siv_city, R.id.siv_own, R.id.siv_school, R.id.siv_age})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_img:
                showSelectPictureDialog();
                break;
            case R.id.siv_nickname:
                Bundle bundleNickname = new Bundle();
                bundleNickname.putString("nickname",mSivNickname.getValue());
                bundleNickname.putInt("type",TYPE_NICKNAME);
                readyGo(ModifyNicknameActivity.class,bundleNickname);
                break;
            case R.id.siv_gender:
                showSelectGenderDialog();
                break;
            case R.id.siv_city:
                readyGo(SelectCityActivity1.class);
                break;
            case R.id.siv_own:
                Bundle bundlePersonal = new Bundle();
                bundlePersonal.putString("content",mSivOwn.getValue());
                readyGo(PersonalProfileActivity.class,bundlePersonal);
                break;
            case R.id.siv_school:
                Bundle bundleSchool = new Bundle();
                bundleSchool.putString("school",mSivSchool.getValue());
                bundleSchool.putInt("type",TYPE_SCHOOL);
                readyGo(ModifyNicknameActivity.class,bundleSchool);
                break;
            case R.id.siv_age:
                showDateDialog();
                break;
        }
    }

    private void showDateDialog() {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
            @Override
            public void onDateChoose(int year, int month, int day) {
                ToastUtils.showToast(year + "-" + month +"-" + day);
                updateDOB(year + "-" + month +"-" + day);
            }
        });
        if( ProfileUtils.sProfileInfo!=null){
            long dob = ProfileUtils.sProfileInfo.getDOB();
            BirthdayToAgeUtil.longToInt(dob);
            datePickerDialogFragment.setSelectedDate(BirthdayToAgeUtil.year,BirthdayToAgeUtil.month,BirthdayToAgeUtil.day);
        }
        datePickerDialogFragment.show(getSupportFragmentManager(), "DatePickerDialogFragment");
    }

    private void updateDOB(String dob) {
        mUserInfoViewModel.updateProfile(2,"DOB",dob);
    }

    private void showSelectGenderDialog() {
        SelectGenderBottomDialog.Builder builder = new SelectGenderBottomDialog.Builder();
        SelectGenderBottomDialog dialog = builder.build();
        builder.setOnSelectPictureListener(isMan -> {
            dialog.dismiss();
            updateGender(isMan);
        });
        dialog.show(getSupportFragmentManager(), "select_picture_dialog");
    }

    private void updateGender(boolean isMan) {
        mUserInfoViewModel.updateProfile(3,"Gender",isMan);
    }


    /**
     * 选择图片的 dialog
     */
    private void showSelectPictureDialog() {
        SelectPictureBottomDialog.Builder builder = new SelectPictureBottomDialog.Builder();
        builder.setOnSelectPictureListener(uri -> {
            //上传图片
            //查询点赞数据
            ThreadManager.getInstance().runOnWorkThread(() ->{
                Bitmap bitmap = ImageUtils.compressImageFromFile(uri.getPath(), 1024f);// 按尺寸压缩图片
                File file = ImageUtils.compressImage(bitmap);  //按质量压缩图片
                Log.e("file","file.length = "+file.length());
                ThreadManager.getInstance().runOnUIThread(() -> {
                    mUserInfoViewModel.uploadAvatar(file);
                });
            });



        });
        SelectPictureBottomDialog dialog = builder.build();
        dialog.show(getSupportFragmentManager(), "select_picture_dialog");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onEventMainThread(CitySelectEvent event) {
        mUserInfoViewModel.updateProfile(2,"Location",event.getCity());
    }


}
