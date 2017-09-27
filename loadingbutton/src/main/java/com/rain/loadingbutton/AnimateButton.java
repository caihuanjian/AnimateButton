package com.rain.loadingbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

/**
 * Created by HwanJ.Choi on 2017-9-25.
 */

public class AnimateButton extends Button {

    private enum State {
        IDLE, LOADING, DONE
    }

    private LoaddingDrawable mCicleLoadingDrawable;

    private GradientDrawable mGradientDrawable;

    private IndicateDrawable mIndicateDrawable;

    private Params mParams;

    private State mCurState;
    private boolean isChanging;//是否处于变形状态

    public AnimateButton(Context context) {
        this(context, null);
    }

    public AnimateButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnimateButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mParams = new Params();
        if (attrs == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mGradientDrawable = (GradientDrawable) context.getDrawable(R.drawable.default_shape);
            } else {
                mGradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.default_shape);
            }
        } else {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton,defStyleAttr,defStyleRes);
            final TypedArray bgTypeArray = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.background},defStyleAttr,defStyleRes);
            try {
                mGradientDrawable = (GradientDrawable) bgTypeArray.getDrawable(0);
            } catch (ClassCastException e) {
                final Drawable drawable = bgTypeArray.getDrawable(0);
                if (drawable instanceof ColorDrawable) {
                    ColorDrawable colorDrawable = (ColorDrawable) drawable;
                    mGradientDrawable = new GradientDrawable();
                    mGradientDrawable.setColor(colorDrawable.getColor());
                } else if (drawable instanceof StateListDrawable) {
                    StateListDrawable stateListDrawable = (StateListDrawable) drawable;
                    try {
                        mGradientDrawable = (GradientDrawable) stateListDrawable.getCurrent();
                    } catch (ClassCastException e1) {
                        throw new RuntimeException("");
                    }
                }
            }
            mParams.mSpinningBarWidth = typedArray.getDimension(R.styleable.LoadingButton_spinning_bar_width, 5);
            mParams.mSpinningBarColor = typedArray.getColor(R.styleable.LoadingButton_spinning_bar_color, Color.WHITE);
            mParams.mPaddingProgress = typedArray.getDimension(R.styleable.LoadingButton_spinning_bar_padding, 0);
            mParams.mInitialCornerRadius = typedArray.getDimension(R.styleable.LoadingButton_initialCornerRadius, 0);
            mParams.mFinalCornerRadius = typedArray.getDimension(R.styleable.LoadingButton_finalCornerRadius, 100);
            mParams.mFinalFillColor = typedArray.getColor(R.styleable.LoadingButton_finalFillColor, Color.BLACK);
            typedArray.recycle();
            bgTypeArray.recycle();
        }
        mParams.mDrawables = getCompoundDrawablesRelative();
        mParams.mText = (String) getText();
        setBackground(mGradientDrawable);

        mCurState = State.IDLE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (!isChanging) {
            if (mCurState == State.LOADING) {
                drawLoadding(canvas);
            } else if (mCurState == State.DONE) {
                drawDoneAnimation(canvas);
            }
        }
        canvas.restore();
    }

    private void drawLoadding(Canvas canvas) {
        if (mCicleLoadingDrawable == null || !mCicleLoadingDrawable.isRunning()) {
            mCicleLoadingDrawable = new LoaddingDrawable(this, mParams.mSpinningBarWidth, mParams.mSpinningBarColor);
            int offsetCenter = (getHeight() - getWidth()) / 2;

            int left = (int) mParams.mPaddingProgress;
            int top = (int) (mParams.mPaddingProgress + offsetCenter);
            int right = (int) (getWidth() - mParams.mPaddingProgress);
            int bottom = (int) (getHeight() - mParams.mPaddingProgress - offsetCenter);
            mCicleLoadingDrawable.setBounds(left, top, right, bottom);
            mCicleLoadingDrawable.setCallback(this);
            mCicleLoadingDrawable.start();
        } else {
            mCicleLoadingDrawable.draw(canvas);
        }
    }

    private void drawDoneAnimation(Canvas canvas) {
        mIndicateDrawable.draw(canvas);
    }

    public void startAnimation() {
        if (mCurState != State.IDLE) {
            return;
        }
        isChanging = true;
        setClickable(false);
        mCurState = State.LOADING;
        setCompoundDrawables(null, null, null, null);
        setText(null);

        mParams.mInitialWidth = getWidth();
        mParams.mInitialHeight = getHeight();

        int toWidth = mParams.mInitialHeight;
        int toHeight = mParams.mInitialHeight;

        ObjectAnimator cornerAnimator = ObjectAnimator.ofFloat(mGradientDrawable, "cornerRadius", mParams.mInitialCornerRadius, mParams.mFinalCornerRadius);

        ValueAnimator widthAnimator = ValueAnimator.ofFloat(mParams.mInitialWidth, toWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = (int) val;
                setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimator = ValueAnimator.ofFloat(mParams.mInitialHeight, toHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = (int) val;
                setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(widthAnimator, heightAnimator, cornerAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isChanging = false;
                setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3]);
            }
        });
        animatorSet.start();
    }

    private void startRevert() {
        if (mCurState != State.DONE) {
            return;
        }
        isChanging = true;

        int fromWidth = getWidth();
        int fromHeight = getHeight();

        int toWidth = mParams.mInitialWidth;
        int toHeight = mParams.mInitialHeight;

        ObjectAnimator cornerAnimator = ObjectAnimator.ofFloat(mGradientDrawable, "cornerRadius", mParams.mFinalCornerRadius, mParams.mInitialCornerRadius);

        ValueAnimator widthAnimator = ValueAnimator.ofFloat(fromWidth, toWidth);
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = (int) val;
                setLayoutParams(layoutParams);
            }
        });
        ValueAnimator heightAnimator = ValueAnimator.ofFloat(fromHeight, toHeight);
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = (int) val;
                setLayoutParams(layoutParams);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(widthAnimator, heightAnimator, cornerAnimator);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isChanging = false;
                setCompoundDrawablesRelative(mParams.mDrawables[0], mParams.mDrawables[1], mParams.mDrawables[2], mParams.mDrawables[3]);
                mCurState = State.IDLE;
                setClickable(true);
                setText(mParams.mText);
            }
        });
        animatorSet.start();
    }

    /**
     * @Description: invoke when loading complete!
     */
    public void loadingComplete(Bitmap indicateBitmap, int fillColor, boolean reverse) {
        if (mCurState != State.LOADING) {
            return;
        }
        mCurState = State.DONE;

        mIndicateDrawable = new IndicateDrawable(this, fillColor, indicateBitmap);
        mIndicateDrawable.setBounds(0, 0, getWidth(), getHeight());
        mIndicateDrawable.animateIndicate();

        if (reverse) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRevert();
                }
            }, 1000);
        }
    }

    private class Params {
        private float mSpinningBarWidth;
        private int mSpinningBarColor;
        private float mPaddingProgress;
        private int mInitialHeight;
        private int mInitialWidth;
        private int mFinalFillColor;
        private String mText;
        private float mInitialCornerRadius;
        private float mFinalCornerRadius;
        private Drawable[] mDrawables;
    }
}
