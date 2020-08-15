package cn.rongcloud.im.niko.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;


/**
 * Created by niko on 2018/10/30.
 */

public abstract class BaseItemView extends RelativeLayout {
    protected Context mContext;
    public BaseItemView(Context context){
        super(context);
        init(context);
    }



    public BaseItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(getContext()).inflate(getLayoutResId(),this);
        ButterKnife.bind(this, this);
        initView();
    }




    protected void initView() {
    }

    public abstract int getLayoutResId();


    public void readyGo(Class<?> clazz, Bundle bundle) {
        if (clazz != null) {
            Intent intent = new Intent(mContext, clazz);
            if (null != bundle) {
                intent.putExtras(bundle);
            }
            mContext.startActivity(intent);
        }
    }

    /**
     * startActivity
     */
    public void readyGo(Class<?> clazz) {
        readyGo(clazz, null);
    }




    public String getStr(int id) {
        return getResources().getString(id);
    }

    public int getCol(int id) {
        return getResources().getColor(id);
    }

    public String getStrFormat(int id, int num) {
        return String.format(getStr(id), num);
    }

    public String getStrFormat(int id, String str) {
        return String.format(getStr(id), str);
    }



}
