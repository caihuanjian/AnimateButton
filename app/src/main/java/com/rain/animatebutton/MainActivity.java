package com.rain.animatebutton;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.rain.loadingbutton.AnimateButton;

public class MainActivity extends AppCompatActivity {

    private Button mBtn;
    private GradientDrawable mDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = (Button) findViewById(R.id.btn);
        Drawable drawable = getResources().getDrawable(R.drawable.shape_default);
        StateListDrawable stateListDrawable = (StateListDrawable) drawable;
        mDrawable = (GradientDrawable) stateListDrawable.getCurrent();

        mBtn.setBackground(mDrawable);
        findViewById(R.id.loadding_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimateButton button = (AnimateButton) v;
                button.startAnimation();
            }
        });
    }

    //    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mDrawable, "cornerRadius", 50, 100);

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
                break;
        }

    }
}
