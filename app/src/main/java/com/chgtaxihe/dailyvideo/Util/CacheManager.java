package com.chgtaxihe.dailyvideo.Util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Created by Administrator on 2017/4/29.
 */

public class CacheManager {

    static final private String synTag = "CacheLock";
    static private CacheManager manager = null;
    static private String mCacheDir;

    public final static int MODE_APPEND = 1;
    public final static int MODE_OVERRIDE = 2;

    private CacheManager(){

    }

    public static CacheManager getInstance(){
        if (manager == null){
            synchronized(synTag){
                if (manager == null){
                    manager = new CacheManager();
                }
            }
        }
        return manager;
    }

    public void setCacheDir(String path){
        if (!path.endsWith(File.separator)){
            mCacheDir = new StringBuilder(path).append(File.separator).toString();
        }else {
            mCacheDir = path;
        }
        File f = new File(mCacheDir);
        if (!f.exists()){
            f.mkdirs();
        }
    }

    public void put(String key,byte[] data,int mode) throws IOException {
        if (mode == MODE_OVERRIDE){
            remove(key);
        }
        RandomAccessFile raf = new RandomAccessFile(mCacheDir + key,"rw");
        long length = raf.length();
        raf.seek(length);
        raf.write(data);
        raf.close();
    }

    public byte[] get(String key){
        File f = new File(mCacheDir + key);
        if (f.exists()){
            try {
                RandomAccessFile raf = new RandomAccessFile(f,"r");
                ByteArrayOutputStream aos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = -1;
                while ((length = raf.read(buffer))!= -1){
                    aos.write(buffer,0,length);
                }
                return aos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param key
     * @return 得到的File，直接向其中写入即可
     */
    public File getFliefromKey(String key){
        return new File(mCacheDir + key);
    }


    public void remove(String key){
        File f = getFliefromKey(key);
        f.delete();
    }

    public boolean isExist(String key){
        File f = getFliefromKey(key);
        return f.exists();
    }

    public float getCacheSize(){
        float size = 0;
        File file = new File(mCacheDir);
        File files[] = file.listFiles();
        for (File f : files){
            size += getFileSize(f);
        }
        return size;
    }

    private float getFileSize(File f) {
        float s = 0;
        if (f.exists()){
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                s = fis.available()/1024;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return s;
    }

    public void cleanCache(){
        File file = new File(mCacheDir);
        File files[] = file.listFiles();
        for (File f : files){
            f.delete();
        }
    }

}
