package com.rain.animatebutton;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rain.loadingbutton.AnimateButton;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            animateButton.loadingComplete(BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_alarm_on_white_48dp), getResources().getColor(R.color.colorPrimary), true);
        }
    };

    private AnimateButton animateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animateButton = (AnimateButton) findViewById(R.id.loadding_btn);

        animateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimateButton button = (AnimateButton) v;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.obtainMessage().sendToTarget();
                    }
                }).start();
                button.startAnimation();
            }
        });

    }
}
