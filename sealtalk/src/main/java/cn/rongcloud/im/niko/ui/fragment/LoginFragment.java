package cn.rongcloud.im.niko.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.heytap.mcssdk.utils.LogUtil;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.model.CountryInfo;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Result;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.model.UserCacheInfo;
import cn.rongcloud.im.niko.model.niko.ProfileInfo;
import cn.rongcloud.im.niko.model.niko.TokenBean;
import cn.rongcloud.im.niko.sp.ProfileUtils;
import cn.rongcloud.im.niko.sp.SPUtils;
import cn.rongcloud.im.niko.task.UserTask;
import cn.rongcloud.im.niko.ui.activity.MainActivity;
import cn.rongcloud.im.niko.ui.activity.SelectCountryActivity;
import cn.rongcloud.im.niko.ui.widget.ClearWriteEditText;
import cn.rongcloud.im.niko.utils.log.SLog;
import cn.rongcloud.im.niko.viewmodel.LoginViewModel;

public class LoginFragment extends BaseFragment {
    private static final int REQUEST_CODE_SELECT_COUNTRY = 1000;
    private ClearWriteEditText phoneNumberEdit;
    private ClearWriteEditText passwordEdit;
    private TextView countryNameTv;
    private TextView countryCodeTv;

    private LoginViewModel loginViewModel;


    @Override
    protected int getLayoutResId() {
        return R.layout.login_fragment_login;
    }

    @Override
    protected void onInitView(Bundle savedInstanceState, Intent intent) {
        phoneNumberEdit = findView(R.id.cet_login_phone);
//        phoneNumberEdit.setText(UserTask.phone);
        passwordEdit = findView(R.id.cet_login_password);
        countryNameTv = findView(R.id.tv_country_name);
        countryCodeTv = findView(R.id.tv_country_code);
        findView(R.id.btn_login, true);
        findView(R.id.ll_country_select, true);

        phoneNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    phoneNumberEdit.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(phoneNumberEdit.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onInitViewModel() {
        loginViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
        loginViewModel.getLoginResult().observe(this, new Observer<Resource<String>>() {
            @Override
            public void onChanged(Resource<String> resource) {
                if (resource.status == Status.SUCCESS) {
                    dismissLoadingDialog(new Runnable() {
                        @Override
                        public void run() {
                            showToast(R.string.seal_login_toast_success);
                            toMain(resource.data);
                        }
                    });

                } else if (resource.status == Status.LOADING) {
                    showLoadingDialog(R.string.seal_loading_dialog_logining);
                } else {
                    dismissLoadingDialog(new Runnable() {
                        @Override
                        public void run() {
                            showToast(resource.message);
                        }
                    });
                }
            }
        });

        loginViewModel.getLastLoginUserCache().observe(this, new Observer<UserCacheInfo>() {
            @Override
            public void onChanged(UserCacheInfo userInfo) {
                phoneNumberEdit.setText(userInfo.getPhoneNumber());
                String region = userInfo.getRegion();
                if (!region.startsWith("+")) {
                    region = "+" + region;
                }
                countryCodeTv.setText(region);
                CountryInfo countryInfo = userInfo.getCountryInfo();
                if (countryInfo != null && !TextUtils.isEmpty(countryInfo.getCountryName())) {
                    countryNameTv.setText(countryInfo.getCountryName());
                }
                passwordEdit.setText(userInfo.getPassword());
            }
        });


        loginViewModel.getGetTokenResult().observe(this, new Observer<TokenBean>() {
            @Override
            public void onChanged(TokenBean tokenBean) {
                if (tokenBean != null && !TextUtils.isEmpty(tokenBean.getAccess_token())) {
                    showToast("成功");
                    //获取msg需要赋值
//                    NetConstant.Authorization = "Bearer "+tokenBean.getAccess_token();
//                    loginViewModel.getSms(phoneNumberEdit.getText().toString().trim());

                    loginViewModel.getUserToken(phoneNumberEdit.getText().toString().trim(),passwordEdit.getText().toString().trim());

                } else {
                    showToast("失败");
                }
            }
        });


        loginViewModel.getGetSmsResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result!=null&&result.RsCode == 3) {
                    showToast("成功");
                    loginViewModel.verifySms(phoneNumberEdit.getText().toString().trim());
                } else {
                    showToast("失败");
                }
            }
        });

        loginViewModel.getVerifyResult().observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                if (result.RsCode == 3) {
                    showToast("成功");
                    loginViewModel.getUserToken(phoneNumberEdit.getText().toString().trim(),passwordEdit.getText().toString().trim());
                } else {
                    showToast("失败");
                }
            }
        });

        loginViewModel.getGetUserTokenResult().observe(this, new Observer<TokenBean>() {
            @Override
            public void onChanged(TokenBean tokenBean) {
                if (tokenBean != null && !TextUtils.isEmpty(tokenBean.getAccess_token())) {
                    showToast("成功");
                    NetConstant.Authorization = "Bearer "+tokenBean.getAccess_token();
                    SPUtils.setUserToken(getContext(),tokenBean.getAccess_token());
                    SPUtils.setLogin(getContext(),true);
                    ProfileUtils.sProfileInfo = new ProfileInfo();
                    ProfileUtils.sProfileInfo.setId(tokenBean.getUID());

                    loginViewModel.login("",phoneNumberEdit.getText().toString().trim(),"");
                } else {
                    showToast("失败");
                }
            }
        });


    }


    @Override
    protected void onClick(View v, int id) {
        switch (id) {
            case R.id.btn_login:
                loginViewModel.getToken();
//                loginViewModel.login("",phoneNumberEdit.getText().toString().trim(),passwordEdit.getText().toString().trim());
                break;
            case R.id.ll_country_select:
//                loginViewModel.getToken();
                break;
            default:
                break;
        }
    }

    /**
     * 登录到 业务服务器，以获得登录 融云 IM 服务器所必须的 token
     *
     * @param region 国家区号
     * @param phone  电话号/帐号
     * @param pwd    密码
     */
    private void login(String region, String phone, String pwd) {
        loginViewModel.login(region, phone, pwd);
    }

    private void toMain(String userId) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(IntentExtra.USER_ID, userId);
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK && requestCode == REQUEST_CODE_SELECT_COUNTRY) {
            CountryInfo info = data.getParcelableExtra(SelectCountryActivity.RESULT_PARAMS_COUNTRY_INFO);
            countryNameTv.setText(info.getCountryName());
            countryCodeTv.setText(info.getZipCode());
        }
    }

    /**
     * 设置上参数
     * @param phone
     * @param region
     * @param countryName
     */
    public void setLoginParams(String phone, String region, String countryName) {
        phoneNumberEdit.setText(phone);
        countryNameTv.setText(countryName);
        countryCodeTv.setText(region);
    }
}
