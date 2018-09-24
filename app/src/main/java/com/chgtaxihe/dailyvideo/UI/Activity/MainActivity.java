package com.chgtaxihe.dailyvideo.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.UI.FragManager;
import com.chgtaxihe.dailyvideo.UI.Fragment.MainFragment;
import com.chgtaxihe.dailyvideo.UI.Fragment.MergedVideoFragment;
import com.chgtaxihe.dailyvideo.UI.Fragment.SettingFragment;
import com.chgtaxihe.dailyvideo.UI.Fragment.VideoFragment;
import com.chgtaxihe.dailyvideo.Util.CacheManager;
import com.chgtaxihe.dailyvideo.Util.MessageUtil;

/**
 * https://github.com/sannies/mp4parser
 * 使用mp4parser合并视频
 */
public class MainActivity extends BaseActivity {

    final public static int RECORD_ACTIVITY_REQUESTCODE = 1;


    private DrawerLayout drawer_layout;
    private ActionBar action_bar;
    private FloatingActionButton floating_button;
    private NavigationView nav_main;
    private LinearLayout layout_share;

    private FragManager mFragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
        initCache();
        initView();
        initFragment();
    }

    /**
     * 所有用于测试用的代码都放于此
     */
    private void test() {

    }

    private void initFragment() {
        mFragManager = FragManager.getInstance();
        mFragManager.setFragmentManager(getFragmentManager());
        //载入VideoFragment
        mFragManager.setLayoutID(R.id.layout_frag);
        mFragManager.add(new VideoFragment(), VideoFragment.FRAGMENT_TAG);
    }

    private void initCache() {
        String s = getExternalCacheDir().getPath();
        if (s == null) {
            s = getCacheDir().getPath();
        }
        CacheManager cacheManager = CacheManager.getInstance();
        cacheManager.setCacheDir(s);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //当restart，刷新
        VideoFragment f = (VideoFragment) mFragManager.getFragment(VideoFragment.FRAGMENT_TAG);
        if (f != null) {
            f.refreshRecycleView();
        }
    }

    private long mFirstBackpressTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - mFirstBackpressTime > 2000) {
                makeToast(getString(R.string.press_again_to_exit));
                mFirstBackpressTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            case R.id.item_help:
                MessageUtil.showDialogMessage(this, getString(R.string.on_btn_help), getString(R.string.btn_ok2));
                break;
        }
        return true;
    }

    private void initView() {
        /**
         * findView
         */
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        action_bar = getSupportActionBar();
        floating_button = (FloatingActionButton) findViewById(R.id.float_button);
        nav_main = (NavigationView) findViewById(R.id.nav_main);
        layout_share = (LinearLayout) findViewById(R.id.layout_share);
        //初始化设置
        nav_main.setCheckedItem(R.id.nav_video);
        /**
         * 注册事件
         */
        if (action_bar != null) {
            action_bar.setDisplayHomeAsUpEnabled(true);
            action_bar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        nav_main.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                //导航栏点击事件注册
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_main:
                        if (mFragManager.getFragment(MainFragment.FRAGMENT_TAG) == null) {
                            mFragManager.add(new MainFragment(), MainFragment.FRAGMENT_TAG);
                        } else {
                            mFragManager.show(MainFragment.FRAGMENT_TAG);
                        }
                        break;
                    case R.id.nav_video:
                        if (mFragManager.getFragment(VideoFragment.FRAGMENT_TAG) == null) {
                            mFragManager.add(new VideoFragment(), VideoFragment.FRAGMENT_TAG);
                        } else {
                            mFragManager.show(VideoFragment.FRAGMENT_TAG);
                        }
                        break;
                    case R.id.nav_record:
                        Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                        startActivityForResult(intent, RECORD_ACTIVITY_REQUESTCODE);
                        break;
                    case R.id.nav_merge:
                        Intent intent1 = new Intent(MainActivity.this, MergeActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.nav_setting:
                        if (mFragManager.getFragment(SettingFragment.FRAGMENT_TAG) == null) {
                            mFragManager.add(new SettingFragment(), SettingFragment.FRAGMENT_TAG);
                        } else {
                            mFragManager.show(SettingFragment.FRAGMENT_TAG);
                        }
                        break;
                    case R.id.nav_merged_video:
                        if (mFragManager.getFragment(MergedVideoFragment.FRAGMENT_TAG) == null) {
                            mFragManager.add(new MergedVideoFragment(), MergedVideoFragment.FRAGMENT_TAG);
                        } else {
                            mFragManager.show(MergedVideoFragment.FRAGMENT_TAG);
                        }
                        break;
                }
                nav_main.setCheckedItem(R.id.nav_video);
                drawer_layout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开分享界面
//                Intent i = new Intent(MainActivity.this,ShareActivity.class);
//                startActivity(i);
                MessageUtil.showDialogMessage(MainActivity.this,"恩，这个功能还没做呢",getString(R.string.btn_ok1));
            }
        });
        /*
        设置ActionBar
         */
        action_bar.setTitle(getString(R.string.app_name));

        floating_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        /**
         * 以下为调试用
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_mainactivity, menu);
        return true;
    }

    //无用方法
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /**
         * 方法来源
         * http://blog.csdn.net/q4878802/article/details/51160424
         * 调用invalidateOptionsMenu()即可刷新
         */
        return super.onPrepareOptionsMenu(menu);
    }

}
