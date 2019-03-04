package com.dijkstra.pricecardlayout.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dijkstra.pricecardlayout.R;

import java.util.Locale;

/**
 * 价格展示布局
 * Created by maoshenbo on 2017/8/14 14:47.
 */

public class PriceCardLayout extends FrameLayout implements View.OnClickListener {
    private Context mContext;
    private boolean mIsFade;//是否覆盖图层
    private DarkFrameLayout mDarkFrameLayout;//图层
    private boolean mIsDisplaying;//fragment是否展开
    private final int DURATION_DISPLAY_TIME = 330;//每个动画的执行时间
    private int mDurationHind = 330;
    private final float DELAY_TIME = 0.1f;//展示子view的时间间隔（比例）
    private boolean mIsAnimating;//是否正在执行动画
    private float mDisplayChildViewY, mBottomChildViewY;
    private float mDisplayViewHeight;
    private ImageView mIvShowPrice;

    public PriceCardLayout(@NonNull Context context) {
        this(context, null);
    }

    public PriceCardLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceCardLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PriceCardLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PriceCardLayout, defStyleAttr, 0);
        mIsFade = typedArray.getBoolean(R.styleable.PriceCardLayout_fade, true);
        typedArray.recycle();
        initBackgroundView();
    }

    private void initBackgroundView() {
        mDarkFrameLayout = new DarkFrameLayout(mContext);
        mDarkFrameLayout.setPriceCard(this);
        addView(mDarkFrameLayout);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findView();
    }

    private void findView() {
        LinearLayout llShowPrice = (LinearLayout) getChildAt(2);
        TextView tvShowPrice = llShowPrice.findViewById(R.id.tv_show_price);
        mIvShowPrice = llShowPrice.findViewById(R.id.iv_show_price);
        tvShowPrice.setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mDisplayViewHeight = getChildAt(1).getHeight();
//        mBottomChildViewY = ViewHelper.getY(getChildAt(2));
        mBottomChildViewY = getChildAt(2).getY();
        mDisplayChildViewY = mBottomChildViewY - mDisplayViewHeight;
//        ViewHelper.setY(getChildAt(1), ViewHelper.getY(getChildAt(2)));
        getChildAt(1).setY(getChildAt(2).getY());
        getChildAt(1).setClickable(false);
        mDurationHind = Integer.parseInt(String.format(Locale.CHINA, "%.0f", (((LinearLayout) getChildAt(1)).getChildCount()) * DURATION_DISPLAY_TIME * DELAY_TIME + DURATION_DISPLAY_TIME));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getY() < ViewHelper.getY(getChildAt(1)) && !mIsAnimating) {//触摸有效区域
        if (ev.getY() < getChildAt(1).getY() && !mIsAnimating) {//触摸有效区域
            if (mIsDisplaying) {
                layoutAnimationHind();
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (mIsDisplaying) {
            layoutAnimationHind();
        } else {
            displayCard();
        }
    }

    private void displayCard() {
        if (mIsDisplaying || mIsAnimating)
            return;
        ValueAnimator displayAnimator = ValueAnimator.ofFloat(mBottomChildViewY, mDisplayChildViewY);
        displayAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        displayAnimator.setDuration(DURATION_DISPLAY_TIME);
        displayAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                ViewHelper.setY(getChildAt(1), value);
                getChildAt(1).setY(value);
                if (mDarkFrameLayout != null && mIsFade) {
                    mDarkFrameLayout.fade((int) (((mBottomChildViewY - value) / mDisplayViewHeight) * DarkFrameLayout.MAX_ALPHA));
                }
            }
        });
        displayAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mIsDisplaying = true;
                mIsAnimating = true;
                layoutAnimationDisplay();
                RotateCard(mIvShowPrice, true);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        displayAnimator.start();
    }

    private void hindCard() {
        ValueAnimator hindAnimator = ValueAnimator.ofFloat(mDisplayChildViewY, mBottomChildViewY);
        hindAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        hindAnimator.setDuration(mDurationHind);
        hindAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
//                ViewHelper.setY(getChildAt(1), value);
                getChildAt(1).setY(value);
                if (mDarkFrameLayout != null && mIsFade) {
                    mDarkFrameLayout.fade((int) (((mBottomChildViewY - value) / mDisplayViewHeight) * DarkFrameLayout.MAX_ALPHA));
                }
            }
        });
        hindAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAnimating = false;
            }
        });
        hindAnimator.start();
    }

    private void layoutAnimationDisplay() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);    //通过加载XML动画设置文件来创建一个Animation对象；
        LayoutAnimationController controller = new LayoutAnimationController(animation);   //得到一个LayoutAnimationController对象；
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);   //设置控件显示的顺序；
        controller.setDelay(DELAY_TIME);   //设置控件显示间隔时间；
        ((LinearLayout) getChildAt(1)).setLayoutAnimation(controller);//为View设置LayoutAnimationController属性；
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ((LinearLayout) getChildAt(1)).startLayoutAnimation();
    }

    private void layoutAnimationHind() {
        if (!mIsDisplaying || mIsAnimating)
            return;
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
        LayoutAnimationController controller = new LayoutAnimationController(animation);
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
        controller.setDelay(DELAY_TIME);
        ((LinearLayout) getChildAt(1)).setLayoutAnimation(controller);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                hindCard();
                mIsAnimating = true;
                mIsDisplaying = false;
                RotateCard(mIvShowPrice, false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ((LinearLayout) getChildAt(1)).startLayoutAnimation();
    }

    private void RotateCard(ImageView imageView, boolean isDisplay) {
        RotateAnimation rotateAnimation;
        if (isDisplay) {
            rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        } else {
            rotateAnimation = new RotateAnimation(180f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        }
        rotateAnimation.setDuration(DURATION_DISPLAY_TIME);
        rotateAnimation.setRepeatCount(0);//设置重复次数
        rotateAnimation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        rotateAnimation.setStartOffset(0);//执行前的等待时间
        imageView.startAnimation(rotateAnimation);
    }

    public boolean ismIsDisplaying() {
        return mIsDisplaying;
    }

    /**
     * 防止oppo等手机锁屏时置位view，导致显示不正确
     */
    public void resetCardView() {
        if (mIsDisplaying) {
//            ViewHelper.setY(getChildAt(1), mDisplayChildViewY);
            getChildAt(1).setY(mDisplayChildViewY);
        }
    }
}
