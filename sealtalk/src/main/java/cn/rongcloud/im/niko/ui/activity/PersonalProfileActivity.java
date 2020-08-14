package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.widget.TitleBar;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;

public class PersonalProfileActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_length)
    AppCompatTextView mTvLength;
    @BindView(R.id.et_content)
    AppCompatEditText mEtContent;
    private TextView mTvSubmit;
    private UserInfoViewModel mUserInfoViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal_profile;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
//        initViewModel();
        mTvSubmit = mTitleBar.getTitleBarTvRight();
        mTvSubmit.setOnClickListener(v -> {
//            mUserInfoViewModel.updateProfile(2,"Bio",mEtContent.getText().toString().trim());
        });
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                mTvLength.setText(String.valueOf(30 - content.length()));
                mTvSubmit.setEnabled(!TextUtils.isEmpty(content));
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String content = bundle.getString("content");
            mEtContent.setText(content);

        }
    }

//    private void initViewModel() {
//        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
//        mUserInfoViewModel.getUpdateProfile().observe(this, profileInfoResult -> {
//            if (profileInfoResult.RsCode == 3) {
//                ProfileUtils.sProfileInfo.setBio(mEtContent.getText().toString().trim());
//                mUserInfoViewModel.getProfileCache().saveUserCache(ProfileUtils.sProfileInfo);
//                EventBus.getDefault().post(new RefreshProfileEvent());
//                finish();
//            }
//        });
//    }
}
