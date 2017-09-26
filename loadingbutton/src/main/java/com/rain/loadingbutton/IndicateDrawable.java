package com.rain.loadingbutton;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by HwanJ.Choi on 2017-9-26.
 */

public class IndicateDrawable extends Drawable {

    private Paint mImagePaint;

    private View mTargetView;

    private Bitmap mIndicateBitmap;
    private Drawable mIndicateDrawable;

    private int mImageAlpha;

    private float mCurRadius;

    private boolean isRunning;

    private float mMaxRadius;
    private float centerX;
    private float centerY;

    private Rect mBounds;

    private boolean canDrawBitmap;

    IndicateDrawable(View targetView, int colorBackGround, Bitmap indicateBitmap) {
        mTargetView = targetView;

        mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mImagePaint.setColor(colorBackGround);
        mImagePaint.setStyle(Paint.Style.FILL);

        mIndicateBitmap = indicateBitmap;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        mImagePaint.setAlpha(255);
        canvas.drawCircle(centerX, centerY, mCurRadius, mImagePaint);

        if (canDrawBitmap) {
            mImagePaint.setAlpha(mImageAlpha);
            mIndicateDrawable.draw(canvas);
//            canvas.drawBitmap(mIndicateBitmap, mBounds.left, mBounds.top, mImagePaint);
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        int width = bounds.width();
        int height = bounds.height();
        int size = Math.min(width, height);
        mMaxRadius = size / 2;
        centerX = (bounds.left + bounds.right) / 2;
        centerY = (bounds.top + bounds.bottom) / 2;
        float offset = 0.35f * size;
        int left = (int) (centerX - offset);
        int top = (int) (centerY - offset);
        int right = (int) (centerX + offset);
        int bottom = (int) (centerY + offset);
        mBounds = new Rect(left, top, right, bottom);

        mIndicateDrawable = new BitmapDrawable(mTargetView.getResources(), mIndicateBitmap);
        mIndicateDrawable.setBounds(mBounds);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mImagePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mImagePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    public void animateIndicate() {
        setupAnimation();
    }

    private void setupAnimation() {
        final ValueAnimator alphaAnimtor = ValueAnimator.ofInt(0, 255);
        alphaAnimtor.setDuration(300);
        alphaAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mImageAlpha = (int) animation.getAnimatedValue();
                mTargetView.invalidate();
            }
        });
        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0, mMaxRadius);
        radiusAnimator.setDuration(400);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurRadius = (float) animation.getAnimatedValue();
            }
        });
        radiusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                canDrawBitmap = true;
                alphaAnimtor.start();
                mTargetView.invalidate();
            }
        });
        radiusAnimator.start();
    }
}
