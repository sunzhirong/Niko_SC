package cn.rongcloud.im.niko.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import cn.rongcloud.im.niko.R;
import io.rong.imkit.widget.AutoLinkTextView;

public class MyAutoLinkTextView extends AutoLinkTextView {
    private int mMaxWidth;
    private int mMinWidth;
    private boolean need;

    public MyAutoLinkTextView(Context context) {
        super(context);
    }

    public MyAutoLinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initWidth(context, attrs);
        this.setAutoLinkMask(7);
    }

    public MyAutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initWidth(context, attrs);
        this.setAutoLinkMask(7);
    }

    @TargetApi(21)
    public MyAutoLinkTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initWidth(context, attrs);
        this.setAutoLinkMask(7);
    }


    private void initWidth(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyAutoLinkTextView);
        this.mMaxWidth = array.getDimensionPixelSize(R.styleable.MyAutoLinkTextView_MyRCMaxWidth, 0);
        this.setMaxWidth(this.mMaxWidth);

        this.mMinWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70 , getContext().getResources()
                .getDisplayMetrics());
        array.recycle();
    }

    public void setNeedMin(boolean need){
        this.need = need;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = this.getLayout();
        float width = 0.0F;

        for(int i = 0; i < layout.getLineCount(); ++i) {
            width = Math.max(width, layout.getLineWidth(i));
        }

        width += (float)(this.getCompoundPaddingLeft() + this.getCompoundPaddingRight());
        if (this.getBackground() != null) {
            width = Math.max(width, (float)this.getBackground().getIntrinsicWidth());
        }

        if (this.mMaxWidth != 0) {
            width = Math.min(width, (float)this.mMaxWidth);
        }

        if(need) {
            if (width < this.mMinWidth) {
                width = mMinWidth;
            }
        }

        this.setMeasuredDimension((int)Math.ceil((double)width), this.getMeasuredHeight());
    }

    public void stripUnderlines() {
        if (this.getText() instanceof Spannable) {
            Spannable s = (Spannable)this.getText();
            URLSpan[] spans = (URLSpan[])s.getSpans(0, s.length(), URLSpan.class);
            URLSpan[] var4 = spans;
            int var5 = spans.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                URLSpan span = var4[var6];
                int start = s.getSpanStart(span);
                int end = s.getSpanEnd(span);
                s.removeSpan(span);
                URLSpan spanNew = new MyAutoLinkTextView.URLSpanNoUnderline(span.getURL());
                s.setSpan(spanNew, start, end, 0);
            }
        }

    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
