package cn.rongcloud.im.niko.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.ui.BaseActivity;
import cn.rongcloud.im.niko.ui.widget.TitleBar;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.viewmodel.EditUserDescribeViewModel;

public class EditUserDescribeActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.et_nickname)
    AppCompatEditText mEtNickname;
    @BindView(R.id.tv_length)
    AppCompatTextView mTvLength;
//    private EditText etDisplayName;
    private String userId;
    private EditUserDescribeViewModel editUserDescribeViewModel;
    public final static int OPERATE_PICTURE_SAVE = 0x1212;
    public final static int OPERATE_PICTURE_DELETE = 0x1211;
    private TextView mTvSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_nickname;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra(IntentExtra.STR_TARGET_ID);
        initView();
        initViewModel();
    }


    private void initView() {
        mTitleBar.setTitle("备注");
        mTvSubmit = mTitleBar.getTitleBarTvRight();
        mTvSubmit.setEnabled(true);
        mTvSubmit.setOnClickListener(v -> {
            setFriendDescription();
        });
        mEtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                mTvLength.setText(String.valueOf(10 - content.length()));
//                mTvSubmit.setEnabled(!TextUtils.isEmpty(content));

            }
        });

    }

    private void initViewModel() {
        editUserDescribeViewModel = ViewModelProviders.of(this,
                new EditUserDescribeViewModel.Factory(getApplication(), userId)).get(EditUserDescribeViewModel.class);
//
        editUserDescribeViewModel.setFriendDescriptionResult().observe(this, new Observer<Resource<Boolean>>() {
            @Override
            public void onChanged(Resource<Boolean> voidResource) {
                if (voidResource.status == Status.SUCCESS) {
                    dismissLoadingDialog();
                    ToastUtils.showToast(R.string.seal_describe_more_btn_set_success);
                    finish();
                } else if (voidResource.status == Status.ERROR) {
                    dismissLoadingDialog();
                    ToastUtils.showToast(R.string.seal_describe_more_btn_set_fail);
                    finish();
                }
            }
        });
    }



    private void setFriendDescription() {
        showLoadingDialog("");
        editUserDescribeViewModel.setFriendDescription(userId, mEtNickname.getText().toString());
    }

}
