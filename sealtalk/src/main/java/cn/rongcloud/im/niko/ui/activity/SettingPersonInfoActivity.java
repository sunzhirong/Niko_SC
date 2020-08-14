package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.event.CitySelectEvent;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.dialog.SelectGenderBottomDialog;
import cn.rongcloud.im.niko.ui.dialog.SelectPictureBottomDialog;
import cn.rongcloud.im.niko.ui.view.SettingItemView;
import cn.rongcloud.im.niko.ui.widget.wheel.date.DatePickerDialogFragment;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

public class SettingPersonInfoActivity extends BaseActivity {

    public static final int TYPE_NICKNAME = 0;
    public static final int TYPE_SCHOOL = 1;
    public static final int TYPE_EMAIL = 2;

    @BindView(R.id.iv_head)
    ImageView mIvHead;
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

//                mUserInfoViewModel.updateProfile(2,"DOB",year + "-" + month +"-" + day);

            }
        });
        datePickerDialogFragment.show(getSupportFragmentManager(), "DatePickerDialogFragment");
    }

    private void showSelectGenderDialog() {
        SelectGenderBottomDialog.Builder builder = new SelectGenderBottomDialog.Builder();
        SelectGenderBottomDialog dialog = builder.build();
        builder.setOnSelectPictureListener(isMan -> {
            dialog.dismiss();
//            mUserInfoViewModel.updateProfile(3,"Gender",isMan);
        });
        dialog.show(getSupportFragmentManager(), "select_picture_dialog");
    }


    /**
     * 选择图片的 dialog
     */
    private void showSelectPictureDialog() {
        SelectPictureBottomDialog.Builder builder = new SelectPictureBottomDialog.Builder();
        builder.setOnSelectPictureListener(uri -> {
            //上传图片
//            uploadPortrait(uri);
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
//        mSivCity.setValue(event.getCity());
//        mUserInfoViewModel.updateProfile(2,"Location",event.getCity());
    }

}
