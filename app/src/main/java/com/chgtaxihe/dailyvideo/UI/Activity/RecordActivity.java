package com.chgtaxihe.dailyvideo.UI.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.Util.CameraHelper;
import com.chgtaxihe.dailyvideo.Util.MessageUtil;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.chgtaxihe.dailyvideo.R.id.drawer_layout;

public class RecordActivity extends BaseActivity{

    final public static int RESULT_SUCCESS = 1;
    final public static int RESULT_FAILD = 2;

    private SurfaceView mView;
    private String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DailyVideo/err.mp4";
    private String PATH2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DailyVideo/";

    private CameraHelper mCameraHelper;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setResult(RESULT_FAILD);
        setContentView(R.layout.activity_record);

        //检查Timemark
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String mark = df.format(new Date()) + ".mp4";
        String s1 = FileManager.getTimeMark(FileManager.VIDEO_FILE_PATH);
        Log.i("tag","mark:" + mark + "\n s1:"+ s1);
        if (mark.equals(s1)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.on_record_regret))
                    .setPositiveButton(getString(R.string.btn_ok1),null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
        }else {
            //初始化文件名
            PATH = new StringBuilder(PATH2).append(mark).toString();
            File f = new File(PATH2);
            if (!f.exists()) {
                f.mkdirs();
            }
            initView();
            reqPermission();//申请权限并打开相机
        }
    }


    private void delFile(String path) {
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
            if(mCameraHelper != null) {
                boolean isRecording = mCameraHelper.isRecording();
                mCameraHelper.releaseCamera();
                if (isRecording){
                    delFile(PATH);
                    makeToast("录像过程请不要切换到其他界面，珍惜这一秒钟吧\nQ_Q");
                    finish();
                }
            }
    }

    /**
     * 只有需要录像时才调用
     */
    private void initRecorder() {
        mCameraHelper.initRecorder(PATH);
    }

    private void initView() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.record_activity_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
        }
        mView = (SurfaceView) findViewById(R.id.surface_view);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraHelper == null || !mCameraHelper.isPreviewReady()){
                    return;
                }
                if (actionBar != null){
                    actionBar.setTitle("正在录制...");
                }
                Log.i("TAG", "开始录像");
                initRecorder();
                try {
                    mCameraHelper.tackVideo(1500,new CameraHelper.OnVideoCallback(){
                        @Override
                        public void OnStop() {
                            finishRecord();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mView.setOnClickListener(null);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (mCameraHelper != null){
                if (mCameraHelper.isRecording()){
                    makeToast(getString(R.string.back_pressed_on_recording));
                }else {
                    finish();
                }
            }else {
                finish();
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mCameraHelper != null){
                    if (mCameraHelper.isRecording()){
                        makeToast(getString(R.string.back_pressed_on_recording));
                        return true;
                    }
                }
                finish();
                break;
        }
        return true;
    }

    private void finishRecord() {
        Intent i = new Intent();
        i.putExtra("path", PATH);
        setResult(RESULT_SUCCESS, i);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "你需要打开摄像头权限才能使用录像功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void reqPermission() {
        //Todo 申请录音权限
        if (Build.VERSION.SDK_INT > 22) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            } else {
                startCamera();
            }
        } else {
            startCamera();
        }
    }


    /**
     * 入口点
     */
    private void startCamera() {
        //Todo 对无摄像头手机的反应
        mCameraHelper = new CameraHelper();
        mCameraHelper.obtainCamera(0);
        if (mCameraHelper.getCamera() == null) {
            makeToast("你的手机没有摄像头?");
            finish();
        }
        mCameraHelper.preparePreview(mView,true);
        makeToast("点击屏幕任意处开始录像，你只有一秒钟的时间哦~");
    }
}
