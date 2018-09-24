package com.chgtaxihe.dailyvideo.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chgtaxihe.dailyvideo.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivtiy extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager welcome_pager;
    //引导页设置
    private final static int[] WELCOME_PIC_ID =
            {R.drawable.welcome_1, R.drawable.welcome_2,R.drawable.welcome_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);
        initView();
    }

    private void initView() {
        welcome_pager = (ViewPager) findViewById(R.id.welcome_pager);
        List<View> v = new ArrayList<>();
        for (int i = 0; i < WELCOME_PIC_ID.length - 1; i++) {
            ImageView imv = new ImageView(WelcomeActivtiy.this);
            imv.setScaleType(ImageView.ScaleType.FIT_XY);
            imv.setImageResource(WELCOME_PIC_ID[i]);
            v.add(imv);
        }
        if (WELCOME_PIC_ID.length != 0) {
            RelativeLayout view = (RelativeLayout) LayoutInflater.from(WelcomeActivtiy.this).inflate(R.layout.item_welcome_pager, null);
            view.setBackgroundResource(WELCOME_PIC_ID[WELCOME_PIC_ID.length - 1]);
            Button btn_welcome = (Button) view.findViewById(R.id.btn_welcome);
            btn_welcome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWelcomeActivity();
                }
            });
            v.add(view);
        }
        welcome_pager.setAdapter(new WelcomeAdapter(v));
        welcome_pager.addOnPageChangeListener(this);

    }

    private void showWelcomeActivity() {
        Intent i = new Intent(WelcomeActivtiy.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class WelcomeAdapter extends PagerAdapter {

        List<View> mView;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mView.get(position));
            return mView.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mView.get(position));
        }

        WelcomeAdapter(List<View> list) {
            mView = list;
        }

        @Override
        public int getCount() {
            return mView.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
