package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.widget.TitleBar;
import cn.rongcloud.im.niko.viewmodel.LoginViewModel;

public class ModifyPwdActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.et_old_pwd)
    EditText mEtOldPwd;
    @BindView(R.id.et_pwd)
    EditText mEtPwd;
    @BindView(R.id.et_confirm_pwd)
    EditText mEtConfirmPwd;
    @BindView(R.id.tv_forget)
    TextView mTvForget;
    @BindView(R.id.tv_tips)
    TextView mTvTips;
    private TextView mTvSubmit;
    private boolean oldPass, newPass, confirmPass;
    private LoginViewModel mLoginViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_pwd;
    }


    private void initView() {
        initViewModel();
        mTvSubmit = mTitleBar.getTitleBarTvRight();
        mTvSubmit.setOnClickListener(v -> {
            if (mEtConfirmPwd.getText().toString().trim().equals(mEtPwd.getText().toString().trim())) {
//                mLoginViewModel.changePw(mEtOldPwd.getText().toString().trim(),mEtConfirmPwd.getText().toString().trim());
            } else {
                mTvTips.setVisibility(View.VISIBLE);
            }
        });
        initEt();

    }

    private void initViewModel() {
//        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
//        mLoginViewModel.getChangePwResult().observe(this,resource -> {
//            if (resource.status == Status.SUCCESS) {
//                dismissLoadingDialog(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                });
//
//            } else if (resource.status == Status.LOADING) {
//                    showLoadingDialog("");
//            } else {
//                    dismissLoadingDialog(new Runnable() {
//                        @Override
//                        public void run() {
//                            showToast(resource.message);
//                        }
//                    });
//            }
//        });
    }

    private void initEt() {
        mEtOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString().trim();
                oldPass = !TextUtils.isEmpty(pwd);
                mTvSubmit.setEnabled(isPass());
            }
        });

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString().trim();
                newPass = !TextUtils.isEmpty(pwd);
                mTvSubmit.setEnabled(isPass());
            }
        });

        mEtConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pwd = s.toString().trim();
                confirmPass = !TextUtils.isEmpty(pwd);
                mTvSubmit.setEnabled(isPass());
            }
        });
    }

    private boolean isPass() {
        return oldPass && newPass && confirmPass;
    }
}
