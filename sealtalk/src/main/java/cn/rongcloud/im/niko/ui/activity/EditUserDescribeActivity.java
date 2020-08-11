package cn.rongcloud.im.niko.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.IntentExtra;
import cn.rongcloud.im.niko.db.model.FriendDescription;
import cn.rongcloud.im.niko.file.FileManager;
import cn.rongcloud.im.niko.model.CountryInfo;
import cn.rongcloud.im.niko.model.Resource;
import cn.rongcloud.im.niko.model.Status;
import cn.rongcloud.im.niko.ui.dialog.CommonDialog;
import cn.rongcloud.im.niko.ui.dialog.SelectPictureBottomDialog;
import cn.rongcloud.im.niko.utils.ImageLoaderUtils;
import cn.rongcloud.im.niko.utils.PhotoUtils;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.log.SLog;
import cn.rongcloud.im.niko.viewmodel.EditUserDescribeViewModel;
import io.rong.imkit.widget.AsyncImageView;

public class EditUserDescribeActivity extends TitleBaseActivity {

    private EditText etDisplayName;
    private String userId;
    private EditUserDescribeViewModel editUserDescribeViewModel;
    public final static int OPERATE_PICTURE_SAVE = 0x1212;
    public final static int OPERATE_PICTURE_DELETE = 0x1211;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_describe);
        userId = getIntent().getStringExtra(IntentExtra.STR_TARGET_ID);
        initView();
        initViewModel();
    }

    private void initView() {
        getTitleBar().setTitle(getString(R.string.profile_set_display_name));
        getTitleBar().setOnBtnRightClickListener(getString(R.string.seal_describe_more_btn_complete), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveConfirmDialog();
            }
        });
        etDisplayName = findViewById(R.id.et_display_name);
    }

    private void initViewModel() {
        editUserDescribeViewModel = ViewModelProviders.of(this,
                new EditUserDescribeViewModel.Factory(getApplication(), userId)).get(EditUserDescribeViewModel.class);
        editUserDescribeViewModel.getFriendDescription().observe(this, new Observer<Resource<FriendDescription>>() {
            @Override
            public void onChanged(Resource<FriendDescription> friendDescriptionResource) {
                if (friendDescriptionResource.status != Status.LOADING && friendDescriptionResource.data != null) {
                    updateView(friendDescriptionResource.data);
                }
            }
        });
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

    private void updateView(FriendDescription friendDescriptionResource) {
        if (!TextUtils.isEmpty(friendDescriptionResource.getDisplayName())) {
            etDisplayName.setText(friendDescriptionResource.getDisplayName(), TextView.BufferType.EDITABLE);
        }
    }

    private void showSaveConfirmDialog() {
        CommonDialog.Builder builder = new CommonDialog.Builder();
        builder.setContentMessage(getString(R.string.seal_describe_more_save_tips));
        builder.setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
            @Override
            public void onPositiveClick(View v, Bundle bundle) {
                setFriendDescription();
            }

            @Override
            public void onNegativeClick(View v, Bundle bundle) {

            }
        });
        CommonDialog deleteDialog = builder.build();
        deleteDialog.show(getSupportFragmentManager().beginTransaction(), "AddCategoriesDialogFragment");
    }

    private void setFriendDescription() {
        showLoadingDialog("");
        editUserDescribeViewModel.setFriendDescription(userId, etDisplayName.getText().toString());
    }

}
