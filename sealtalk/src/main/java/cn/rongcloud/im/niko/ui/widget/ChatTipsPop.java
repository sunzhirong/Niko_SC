package cn.rongcloud.im.niko.ui.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.rongcloud.im.niko.R;

public class ChatTipsPop extends PopupWindow {

    private int popupWidth;
    private int popupHeight;
    private final TextView mTvLeft;
    private final TextView mTvRight;
    private final View mLine;

    public ChatTipsPop(Activity context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_chat_bottom, null);
        // 设置可以获得焦点
        setFocusable(true);
        // 设置弹窗内可点击
        setTouchable(true);
        // 设置弹窗外可点击
        setOutsideTouchable(true);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new BitmapDrawable());

        mTvLeft = (TextView) view.findViewById(R.id.tv_left);
        mTvRight = (TextView) view.findViewById(R.id.tv_right);
        mLine =  view.findViewById(R.id.line);

//        setAnimationStyle(R.style.popup_animation);
        setContentView(view);
        //获取自身的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();

        Log.e("ChatTipsPop",popupHeight+"---"+popupWidth);
    }
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, v.getContext().getResources()
                .getDisplayMetrics());
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2 - margin, location[1] - popupHeight);
    }

    public void setLeftRightCount(int friendsRequestCount, int unreadMsgCount) {
//        unreadMsgCount = 1;
        if(friendsRequestCount==0){
            mTvLeft.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
        }
        if(unreadMsgCount==0){
            mTvRight.setVisibility(View.GONE);
            mLine.setVisibility(View.GONE);
        }
        if(friendsRequestCount==0||unreadMsgCount==0) {
            popupWidth = popupWidth / 2;
        }
        mTvLeft.setText(String.valueOf(friendsRequestCount));
        mTvRight.setText(String.valueOf(unreadMsgCount));
    }
}
