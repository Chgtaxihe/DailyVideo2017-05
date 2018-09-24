package com.chgtaxihe.dailyvideo.Thread;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.UI.Activity.MergeActivity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.chgtaxihe.dailyvideo.File.FileManager.getDateFromName;
import static com.chgtaxihe.dailyvideo.File.FileManager.getVideoFileList;

/**
 *
 * 教训：service必须要有一个默认的构造器
 */

public class MergeService extends Service {

    private MergeBinder mBinder = new MergeBinder();
    private MergeActivity mActivity;
    private String mPath;
    private String mName;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPath = intent.getStringExtra("path");
        mName = intent.getStringExtra("name");
        Log.i("TAG","服务启动");
        MergeVideoTask task = new MergeVideoTask();
        task.execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MergeBinder extends Binder{
        public void setActivity(MergeActivity activity){
            mActivity = activity;
        }
    }

    class MergeVideoTask extends AsyncTask<Void,Void,Void> {

        private FileManager.VidDate date = null;

        @Override
        protected Void doInBackground(Void... params) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String s = df.format(new Date())+ ".mp4";
            date = getDateFromName(s);
            File f = new File(mPath);
            if (!f.exists()){
                f.mkdirs();
            }
            FileManager.mergeVideo(date,mPath + mName);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<Video> videoList = getVideoFileList();
            //遍历适合的Video
            for(Video v : videoList){
                if (!getDateFromName(v.name).isAfter(date)){
                    File f = new File(v.path);
                    f.delete();
                    //合并合适的
                }
            }

            if (mActivity!= null){
                mActivity.handler.sendEmptyMessage(0);
                mActivity = null;
            }

        }
    }

}
