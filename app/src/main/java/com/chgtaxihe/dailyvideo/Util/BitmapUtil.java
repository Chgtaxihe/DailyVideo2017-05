package com.chgtaxihe.dailyvideo.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaMetadataRetriever;

/**
 * Created by Administrator on 2017/4/29.
 */

public class BitmapUtil {

    //可能导致ANR，故得到的图片应先经过处理
    static public Bitmap getPicFormVideo(String path){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Bitmap b = null;
        try {
            retriever.setDataSource(path);
            b = retriever.getFrameAtTime();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                retriever.release();
            }catch (Exception e){

            }
            if (b != null)
                return b;
        }
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(20f);
        b = Bitmap.createBitmap(320,180, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(b);
        canvas.drawText("抱歉，该视频预览图加载失败",0f,90f,p);
        return b;
    }
}
