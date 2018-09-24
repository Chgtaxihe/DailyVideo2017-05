package com.chgtaxihe.dailyvideo.Util;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Chgtaxihe on 2017/4/23.
 */

public class CameraHelper {
    //Todo 做一个定时的AutoFocus-er
    private static final int DEFAULT_VIDEO_BITRATE = (int)(2.5f * 1024 * 1024);

    private Camera mCamera;
    private boolean isReady = false;
    private boolean isPreviewReady = false;
    private boolean isRecordReady = false;
    private MediaRecorder mRecorder = null;
    private boolean isRecording = false;
    private Surface mSurface = null;
    private OnVideoCallback mOnVideoCallback = null;
    private boolean isAutoFocus = false;

    public CameraHelper(){

    }

    public void releaseCamera(){
        isReady = false;
        isPreviewReady = false;
        isRecordReady = false;
        if (mRecorder != null){
            releaseRecorder();
            mRecorder = null;
        }
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void obtainCamera(int id){
        if (mCamera != null){
            mCamera.release();
        }
        isReady = false;
        try {
            mCamera = Camera.open(id);
            isReady = true;
        }catch (RuntimeException e){
            printException(e);
        }
    }

    public void startPreview() throws Exception{
        if (!isPreviewReady) {
            throw new Exception("你得先setPreviewView()并等待其完成");
        }
        mCamera.startPreview();
        if(isAutoFocus) {
            mCamera.autoFocus(null);
        }
    }

    public void stopPreview(){
        mCamera.stopPreview();
    }

    /**
     * @param sv 用于预览的SurfaceView
     * @param startPreview 在准备好后是否开始预览
     */
    public void preparePreview(SurfaceView sv, final boolean startPreview){
        SurfaceHolder holder = sv.getHolder();

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setKeepScreenOn(true);
        initPreviewPara(mCamera);

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    isPreviewReady = true;
                    mSurface = holder.getSurface();
                    if (startPreview){
                        mCamera.startPreview();
                        if(isAutoFocus) {
                            mCamera.autoFocus(null);
                        }
                    }
                } catch (Exception e) {
                    printException(e);
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                try {
                    mCamera.stopPreview();
                }catch (Exception e){
                    //防止Camera释放后，surface才被摧毁而产生的异常
                }
                isPreviewReady = false;
            }
        });
    }

    public void initPreviewPara(Camera c){
        Camera.Parameters p = c.getParameters();
        c.setDisplayOrientation(90);//90度旋转
        List<Camera.Size> listSize = p.getSupportedPreviewSizes();
        Camera.Size size = getSuitableSize(listSize,640,480);//选择适合的预览大小
        //选择合适的对焦模式
        p.setPreviewSize(size.width,size.height);
        String focusMode = "";
        List<String> focusList = p.getSupportedFocusModes();
        for (String f : focusList){
            if (f.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
                focusMode = f;
            }
        }
        if (focusMode.equals("")){
            focusMode = focusList.get(0);
        }
        if (focusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO)){
            isAutoFocus = true;
        }
        p.setFocusMode(focusMode);

        listSize.clear();//回收
        focusList.clear();
        c.setParameters(p);
    }

    private Camera.Size getSuitableSize(List<Camera.Size> listSize,int wid,int hei){
        Camera.Size maxSize = null;
        Camera.Size suitSize = null;
        for (Camera.Size s : listSize) {
            Log.i("Size","Size:" + s.width +"*" +s.height);
            if (maxSize == null){
                maxSize = s;
            }else {
                if (maxSize.width < s.width)
                    maxSize = s;
            }
            if (s.width >= wid && s.height >= hei) {
                if (suitSize == null){
                    suitSize = s;
                }else {
                    if (suitSize.width > s.width && suitSize.height > s.height)
                        suitSize = s;
                }
            }
        }

        if (suitSize != null)
            return suitSize;
        else
            return maxSize;
    }

    private void printException(Exception e){
        e.printStackTrace();
    }

    public void tackPic(Camera.PictureCallback cb){
        setPicturePara(mCamera);
        mCamera.takePicture(null, null,cb);
    }

    public class PICcallback implements Camera.PictureCallback {
        private File save;
        public PICcallback(File File){
            save = File;
        }
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(save);
                os.write(data,0,data.length);
                os.flush();
            } catch (Exception e) {
                printException(e);
            }finally{
                if (os != null){
                    try {
                        os.close();
                    } catch (IOException e) {
                        printException(e);
                    }
                }
            }
            //根据doc，照相后应重新startPreview
            try {
                camera.startPreview();
            }catch (Exception e){
                printException(e);
            }
        }
    }

    public PICcallback getPICcallback(File save){
        return new PICcallback(save);
    }

    private void setPicturePara(Camera c){
        Camera.Parameters p = c.getParameters();
        p.setRotation(90);
        p.setPictureFormat(ImageFormat.JPEG);
        p.setJpegQuality(100);
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        c.setParameters(p);
    }

    /**
     * 先获取Cam后，方可调用该方法
     * 注意，要保证saveFile所指向的文件夹存在，否则出错
     */
    public void initRecorder(String saveFile,Surface previewSurface){
        if (mRecorder != null){
            releaseRecorder();
        }
        Camera.Parameters p = mCamera.getParameters();
        List<Camera.Size> listSize = p.getSupportedVideoSizes();
        Camera.Size suitableSize = getSuitableSize(listSize,1920,1080);//1080P
        listSize.clear();
        mRecorder = new MediaRecorder();
        //必须要解锁才能交给MediaRecorder录像
        mCamera.unlock();
        mRecorder.setCamera(mCamera);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //使用H264封装格式
        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置比特率
        mRecorder.setVideoEncodingBitRate(DEFAULT_VIDEO_BITRATE);
        mRecorder.setVideoSize(suitableSize.width,suitableSize.height);
        //Todo 判断内存空间是否足够
        mRecorder.setOutputFile(saveFile);
        mRecorder.setOrientationHint(90);
        //设置预览
        mRecorder.setPreviewDisplay(previewSurface);
        //设置时长度--小米tmd的好像不支持!
//        mRecorder.setMaxDuration(1000);
        mRecorder.setOnInfoListener(null);
        //据说这样子能防止视频过短而崩溃
        mRecorder.setOnErrorListener(null);
        try {
            mRecorder.prepare();
            isRecordReady = true;
        } catch (IOException e) {
            printException(e);
            isRecordReady = false;
            mRecorder.release();
        }
    }

    /**
     * 先获取Cam并设置Preview后，方可调用该方法
     */
    public void initRecorder(String saveFile){
        initRecorder(saveFile,mSurface);
    }

    public Camera getCamera() {
        return mCamera;
    }

    public boolean isRecording() {
        return isRecording;
    }

    private void releaseRecorder(){
        if (mRecorder != null){
            mOnVideoCallback = null;
            stopRecord();
            mRecorder.release();
        }
    }

    /**
     * 录像
     * @param time
     */
    public void tackVideo(long time,OnVideoCallback cb) throws Exception{
        if(!isRecordReady) {
            throw new Exception("初始化录像失败，无法启动录像");
        }
        mRecorder.start();
        isRecording = true;
        mOnVideoCallback = cb;
        //http://www.cnblogs.com/wansho/p/5104335.html
        if (time != 0) {
            CountDownTimer timer = new CountDownTimer(time, time) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    try {
                        stopRecord();
                    } catch (Exception e) {
                        printException(e);
                    }
                }
            };
            timer.start();
        }
    }

    public void startRecord(OnVideoCallback cb) throws Exception{
        tackVideo(0,cb);
    }

    public void stopRecord(){
        if (isRecording){
            mRecorder.stop();
            if (mOnVideoCallback != null){
                mOnVideoCallback.OnStop();
            }
        }
        isRecording = false;
    }

    public interface OnVideoCallback{
        void OnStop();
    }

    public boolean isPreviewReady() {
        return isPreviewReady;
    }

    public boolean isRecordReady() {
        return isRecordReady;
    }
}
