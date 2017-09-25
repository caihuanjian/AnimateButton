package com.rain.animatebutton;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtn;
    private GradientDrawable mDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new TestView(this));
//        setContentView(R.layout.activity_main);
//        mBtn = (Button) findViewById(R.id.btn);
//        mDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.default_shape);
//        mBtn.setBackground(mDrawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDrawable, "cornerRadius", 50, 0);

        ValueAnimator widthAnimator = ValueAnimator.ofFloat(mBtn.getWidth(), mBtn.getHeight());
        widthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mBtn.getLayoutParams();
                layoutParams.width = (int) val;
                mBtn.setLayoutParams(layoutParams);
            }
        });


        ValueAnimator heightAnimator = ValueAnimator.ofFloat(mBtn.getHeight(), mBtn.getHeight());
        heightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float animatedValue = (float) animation.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = mBtn.getLayoutParams();
                layoutParams.height = (int) animatedValue;
                mBtn.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.setDuration(5000);
        animatorSet.playTogether(objectAnimator, widthAnimator, heightAnimator);
        animatorSet.start();
    }
}
