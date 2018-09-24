package com.chgtaxihe.dailyvideo.UI.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.chgtaxihe.dailyvideo.R;

public class SplashActivity extends AppCompatActivity {

    final static int DELAY_TIME = 3000;
    final static int DELAY_WELCOME = 1000;
    private final static String KEY_IS_FIRST_START = "isFirstStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        if (isFirstStart()){
            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putBoolean(KEY_IS_FIRST_START,false);
            editor.apply();
            showWelcomeActvity();
        }else {
            showActvity();
        }

    }

    private void initView() {

    }

    private void showActvity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },DELAY_TIME);
    }

    private void showWelcomeActvity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,WelcomeActivtiy.class);
                startActivity(i);
                finish();
            }
        },DELAY_WELCOME);
    }

    private boolean isFirstStart(){
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        return sp.getBoolean(KEY_IS_FIRST_START,true);
    }
}
