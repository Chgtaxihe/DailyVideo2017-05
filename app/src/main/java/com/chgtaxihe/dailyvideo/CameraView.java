package com.chgtaxihe.dailyvideo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Administrator on 2017/4/1.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    private Camera mCamera;

    public CameraView(Context context,Camera c) {
        super(context);
        this.mCamera =c;
        this.getHolder().addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
