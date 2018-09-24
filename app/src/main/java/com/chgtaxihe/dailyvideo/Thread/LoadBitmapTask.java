package com.chgtaxihe.dailyvideo.Thread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.chgtaxihe.dailyvideo.Util.BitmapUtil;
import com.chgtaxihe.dailyvideo.Util.CacheManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/29.
 * 本类负责加载MainActivity中的ImageView的图片
 * 通过视频路径取得其略缩图，并写入Cache中
 */

public class LoadBitmapTask extends AsyncTask <LoadBitmapTask.CLoader,LoadBitmapTask.CPasser,Boolean>{

    final static private String CACHE_HEAD_NAME = "bmpcache_";

    @Override
    protected void onProgressUpdate(CPasser... values) {
        int l = values.length;
        for (int i = 0;i<l;i++){
            values[i].imageView.setImageBitmap(values[i].bmp);
        }
    }

    @Override
    protected Boolean doInBackground(CLoader... params) {
        int l = params.length;
        CacheManager cache = CacheManager.getInstance();
        for (int i = 0;i<l;i++){
            //Todo 由于RecycleView 对 View的复用，来回滚动Recycleview会导致图片多次加载，此问题需要解决
            CPasser cp = new CPasser();
            cp.imageView = params[i].imageView;

            File f = new File(params[i].path);
            String key = CACHE_HEAD_NAME + f.getName();
            if (cache.isExist(key)){
                byte[] data = cache.get(key);
                cp.bmp = BitmapFactory.decodeByteArray(data,0,data.length);
            }else {
                cp.bmp = BitmapUtil.getPicFormVideo(params[i].path);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(cache.getFliefromKey(key));
                    cp.bmp.compress(Bitmap.CompressFormat.JPEG,80,fos);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(cp);
            if (isCancelled()) break;
        }
        return true;
    }

    static public class CLoader{
        public ImageView imageView;
        public String path;
    }

    public class CPasser{
        public ImageView imageView;
        public Bitmap bmp;
    }
}
