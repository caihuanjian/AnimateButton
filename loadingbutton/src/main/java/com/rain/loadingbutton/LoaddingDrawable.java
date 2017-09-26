package com.rain.loadingbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by HwanJ.Choi on 2017-9-25.
 */

public class LoaddingDrawable extends Drawable {

    private Paint mPaint;
    private float mBorderWidth;

    private View mTargetView;

    private float mCurrentGlobalAngle;
    private float mCurrentSweepAngle;
    private float mCurrentGlobalAngleOffset;

    private ValueAnimator mValueAnimatorAngle;
    private ValueAnimator mValueAnimatorSweep;
    private AnimatorSet mAnimatorSet;
    private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator SWEEP_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final int ANGLE_ANIMATOR_DURATION = 2000;
    private static final int SWEEP_ANIMATOR_DURATION = 700;
    private static final Float MIN_SWEEP_ANGLE = 50f;

    private RectF boundF;

    private boolean isGrowing;

    private boolean isRunning;

    LoaddingDrawable(View target, float borderWidth, int arcColor) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(arcColor);
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setAntiAlias(true);

        mBorderWidth = borderWidth;
        mTargetView = target;

        boundF = new RectF();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
        float sweep = mCurrentSweepAngle;//0~360-2*min
        if (!isGrowing) {
            startAngle += sweep;
            sweep = 360 - sweep - MIN_SWEEP_ANGLE;//min~360-min
        } else {
            sweep += MIN_SWEEP_ANGLE;//从最小值累加
        }
        canvas.drawArc(boundF, startAngle, sweep, false, mPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        final float padding = mBorderWidth / 2f + .6f;//加0.几的偏移，不然会loadding会绘制到背景之外，实在想不明白。。。。
        boundF.left = bounds.left + padding;
        boundF.right = bounds.right - padding;
        boundF.top = bounds.top + padding;
        boundF.bottom = bounds.bottom - padding;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    private boolean shouldDraw;

    private void setupAnimator() {
        mAnimatorSet = new AnimatorSet();

        mValueAnimatorAngle = ValueAnimator.ofFloat(0, 360f);
        mValueAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
        mValueAnimatorAngle.setDuration(ANGLE_ANIMATOR_DURATION);
        mValueAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimatorAngle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentGlobalAngle = (float) animation.getAnimatedValue();

            }
        });

        mValueAnimatorSweep = ValueAnimator.ofFloat(0, 360 - 2 * MIN_SWEEP_ANGLE);
        mValueAnimatorSweep.setDuration(SWEEP_ANIMATOR_DURATION);
        mValueAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
        mValueAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimatorSweep.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentSweepAngle = (float) animation.getAnimatedValue();
                if (mCurrentSweepAngle < 5) {
                    shouldDraw = true;
                }
                if (shouldDraw) {
                    mTargetView.invalidate();
                }
            }
        });
        mValueAnimatorSweep.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                togleMode();
                shouldDraw = false;
            }
        });
        mAnimatorSet.playTogether(mValueAnimatorAngle, mValueAnimatorSweep);
        mAnimatorSet.start();
    }

    private void togleMode() {
        isGrowing = !isGrowing;
        if (isGrowing) {
            mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + 2 * MIN_SWEEP_ANGLE) % 360;
        }
    }

    public void start() {
        if (isRunning()) {
            return;
        }
        isRunning = true;
        setupAnimator();
    }

    public void stop() {
        if (!isRunning()) {
            return;
        }
        isRunning = false;
        if (mAnimatorSet != null && mAnimatorSet.isRunning()) {
            mAnimatorSet.cancel();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}
