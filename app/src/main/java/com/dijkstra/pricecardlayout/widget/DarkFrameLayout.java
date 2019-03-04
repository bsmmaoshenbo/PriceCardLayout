package com.dijkstra.pricecardlayout.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 价格展示图层
 * Created by maoshenbo on 2017/8/14 14:47.
 */
class DarkFrameLayout extends FrameLayout {

    public final static int MAX_ALPHA = 0x80;
    private Paint mFadePaint;
    private int mAlpha = 0x00;
    private PriceCardLayout mPriceCardLayout;

    public DarkFrameLayout(Context context) {
        this(context, null);
    }

    public DarkFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DarkFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFadePaint = new Paint();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawFade(canvas);
    }

    private void drawFade(Canvas canvas) {
        mFadePaint.setColor(Color.argb(mAlpha, 0, 0, 0));
        canvas.drawRect(0, 0, getMeasuredWidth(), getHeight(), mFadePaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mPriceCardLayout.ismIsDisplaying();
    }

    public void fade(int alpha) {
        this.mAlpha = alpha;
        invalidate();
    }

    public void setPriceCard(PriceCardLayout mPriceCardLayout) {
        this.mPriceCardLayout = mPriceCardLayout;
    }

}
