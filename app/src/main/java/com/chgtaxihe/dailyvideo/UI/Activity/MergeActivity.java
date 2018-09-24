package com.chgtaxihe.dailyvideo.UI.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.Thread.MergeService;
import com.chgtaxihe.dailyvideo.Util.CacheManager;
import com.chgtaxihe.dailyvideo.Util.MessageUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.chgtaxihe.dailyvideo.R.id.drawer_layout;

public class MergeActivity extends BaseActivity implements View.OnClickListener{

    private ActionBar actionBar;
    private Button btn_merge;
    private ImageView imv_merge;

    private String mergePath;
    private List<Video> mVideos;
    private boolean isMerging = false;
    private boolean canMerge = true;
    private String timemark;
    public Handler handler = new mHandler();

    class mHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            OnMergeFinish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        initVideo();
        initView();
    }

    private void initVideo() {
        mVideos = FileManager.getVideoFileList();
    }

    private void initView() {
        actionBar = getSupportActionBar();
        btn_merge = (Button) findViewById(R.id.btn_merge);
        imv_merge = (ImageView) findViewById(R.id.imv_merge);

        btn_merge.setOnClickListener(this);

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        startAnim();

        actionBar.setTitle("合并"+mVideos.size()+"个视频");

        if (mVideos.size() < 3){
            MessageUtil.showDialogMessage(this,getString(R.string.on_merge_too_few_video),getString(R.string.btn_ok1));
            canMerge = false;
        }

    }

    private void startAnim(){
        AnimationDrawable anim;
        anim = (AnimationDrawable)imv_merge.getBackground();
        anim.stop();
        anim.start();
    }

    private void stopAnim(){
        AnimationDrawable anim;
        anim = (AnimationDrawable)imv_merge.getBackground();
        anim.stop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_merge:
                if (canMerge) {
                    mergeVideo();
                }else {
                    MessageUtil.showDialogMessage(this,getString(R.string.on_merge_too_few_video),getString(R.string.btn_ok1));
                }
                break;
        }
    }

    //合并时不让退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (isMerging){
            MessageUtil.showDialogMessage(this,getString(R.string.on_exit_merging),getString(R.string.btn_ok1));
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!isMerging){
                    finish();
                }else {//合并时不让退出
                    MessageUtil.showDialogMessage(this,getString(R.string.on_exit_merging),getString(R.string.btn_ok1));
                }
                break;
        }
        return true;
    }

    private void mergeVideo(){
        int size = mVideos.size();
        if(size == 0){
            Toast.makeText(getApplication(),"你还没有录制任何视频",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isMerging) {
            isMerging = true;
            //start
            Video mv = mVideos.get(mVideos.size() - 1);
            timemark = mv.name;
            mergePath = FileManager.MERGED_FILE_PATH;
            btn_merge.setOnClickListener(null);
            Intent startService = new Intent(this,MergeService.class);
            startService.putExtra("path",mergePath);
            //Todo 修复:如果用户故意添加一个"2099-09-99.mp4"文件，那么合并出来的视频的名字会~~~
            String tmp = mVideos.get(0).name;
            String fileName =
                    new StringBuilder(tmp.substring(0,tmp.length()-4))
                    .append("到")
                    .append(mVideos.get(size - 1).name).toString();

            startService.putExtra("name",fileName);

            conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    ((MergeService.MergeBinder)service).setActivity(MergeActivity.this);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            };
            //https://developer.android.com/reference/android/content/Context.html#BIND_AUTO_CREATE
            bindService(startService,conn,BIND_AUTO_CREATE);
            startService(startService);
        }
    }

    private ServiceConnection conn;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn != null) {
            unbindService(conn);
        }
    }

    private void OnMergeFinish(){
        unbindService(conn);
        conn = null;
        FileManager.addTimeMark(FileManager.VIDEO_FILE_PATH,timemark);
        CacheManager.getInstance().cleanCache();
        Toast.makeText(getApplication(),"合并完成",Toast.LENGTH_SHORT).show();
        isMerging = false;
    }
}
