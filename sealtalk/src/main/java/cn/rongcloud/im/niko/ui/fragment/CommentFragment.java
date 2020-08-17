package cn.rongcloud.im.niko.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;


import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.rongcloud.im.niko.R;
import cn.rongcloud.im.niko.common.NetConstant;
import cn.rongcloud.im.niko.event.ItemCommentEvent;
import cn.rongcloud.im.niko.event.SelectCompleteEvent;
import cn.rongcloud.im.niko.model.niko.CommentBean;
import cn.rongcloud.im.niko.model.niko.FollowBean;
import cn.rongcloud.im.niko.net.request.CommentAtReq;
import cn.rongcloud.im.niko.ui.adapter.CommentRvAdapter;
import cn.rongcloud.im.niko.utils.ToastUtils;
import cn.rongcloud.im.niko.utils.log.SLog;
import cn.rongcloud.im.niko.viewmodel.UserInfoViewModel;
import io.rong.eventbus.EventBus;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class CommentFragment extends BaseFragment {

    @BindView(R.id.et_search)
    EditText mEtSearch;
    @BindView(R.id.rv_comment)
    RecyclerView mRvComment;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.et_input)
    EditText mEtInput;
    @BindView(R.id.ll_input)
    LinearLayout mLlInput;
    private int position = 0;
    private UserInfoViewModel mUserInfoViewModel;
    private CommentRvAdapter mCommentRvAdapter;
    private CommentBean mCommentBean;

    public LinearLayout getLlInput(){
        return mLlInput;
    }

    public static CommentFragment getInstance(int position) {
        CommentFragment sf = new CommentFragment();
        sf.position = position;
        return sf;
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_comment;
    }

    @Override
    public void onCreateView(View view) {
        super.onCreateView(view);
        view.setTag(position);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState, Intent intent) {
        EventBus.getDefault().register(this);
        mRvComment.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CommentBean> commentBeans = new ArrayList<>();
        mCommentRvAdapter = new CommentRvAdapter(getContext(), commentBeans);
        mRvComment.setAdapter(mCommentRvAdapter);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mUserInfoViewModel.getCommentList(NetConstant.SKIP, NetConstant.TAKE);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });

        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SLog.e("input", s + "start = " + start + "before = " + before + "count = " + count);
                if (!TextUtils.isEmpty(s.toString()) && String.valueOf(s.charAt(s.length() - 1)).equals("@") && before == 0) {
//                    readyGo(SelectAtPersonActivity.class);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                onSubmit();
            }
            return false;
        });
    }

    @Override
    protected void onInitViewModel() {
        mUserInfoViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        mUserInfoViewModel.getCommentListResult().observe(this, result -> {
            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
                List<CommentBean> rsData = result.RsData;
                mCommentRvAdapter.setDatas(rsData);
            }
            mRefreshLayout.finishRefresh();
        });
        mUserInfoViewModel.getCommentList(NetConstant.SKIP, NetConstant.TAKE);

        mUserInfoViewModel.getCmtAddResult().observe(this, result -> {
            if (result.RsCode == NetConstant.REQUEST_SUCCESS_CODE) {
                mCommentBean = null;
                hideKeyboard(mEtInput);
                mLlInput.setVisibility(View.GONE);
                ToastUtils.showToast("已回复评论");
                mEtInput.setText("");
            }

        });
    }

    public void onEventMainThread(SelectCompleteEvent event) {
        String result = "";
        for (FollowBean bean : event.list){
            result = result.concat("@").concat(bean.getName()).concat(" ");
        }
        SLog.e("niko", result);
        mEtInput.setText(mEtInput.getText()+result);
        mEtInput.setSelection(mEtInput.getText().toString().length());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    public void onEventMainThread(ItemCommentEvent event) {
        mCommentBean = event.getCommentBean();
        mEtInput.setText("");
        mLlInput.setVisibility(View.VISIBLE);
        mEtInput.setHint("回复：" + mCommentBean.getUserHead().getName());
        showInput();
    }

    public void showInput() {
        mEtInput.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEtInput, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        CommentAtReq commentAtBean = new CommentAtReq();
        commentAtBean.setMmID(mCommentBean.getMmID());
        String msg = mEtInput.getText().toString().trim();
        commentAtBean.setMsg(msg);
        commentAtBean.setTCmID(mCommentBean.getCmID());
        List<CommentAtReq.AtUIDsBean> atUIDsBeans = new ArrayList<>();
        commentAtBean.setAtUIDs(atUIDsBeans);
        mUserInfoViewModel.cmtAdd( commentAtBean);
    }
}