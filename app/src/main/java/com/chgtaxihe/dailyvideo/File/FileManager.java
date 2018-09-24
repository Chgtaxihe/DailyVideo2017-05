package com.chgtaxihe.dailyvideo.File;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.googlecode.mp4parser.authoring.tracks.h264.SliceHeader.SliceType.P;

/**
 * Created by Administrator on 2017/4/2.
 */

public class FileManager {

    /**
     * 默认根目录，以"/"结尾
     */
    public static String VIDEO_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DailyVideo/";
    public static String MERGED_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DailyVideo/merge/";

    public static int getVideoFileCount() {
        File folder = new File(VIDEO_FILE_PATH);
        File[] files = folder.listFiles();
        //Todo 还需要判断文件是否为用户手动添加进去的
        //1.判断视频时间长度 0.8<t<1.5 (MediaRecorder停止录像时可能有延迟)
        //2.判断文件名 -done
        if (files != null) {
            return files.length;
        } else {
            return 0;
        }
    }

    public static List<Video> getVideoFileList() {
        Log.i("TAG", "目录为" + VIDEO_FILE_PATH);
        ArrayList<Video> l = new ArrayList<>();
        File folder = new File(VIDEO_FILE_PATH);
        File[] files = folder.listFiles(new NameFilter());
        if (files != null) {
            for (File f : files) {
                l.add(new Video(f.getAbsolutePath(), f.getName()));
            }
        }
        Log.i("FileManager", "有" + l.size() + "个文件");
        return l;
    }

    public static List<Video> getMergedVideos() {
        ArrayList<Video> l = new ArrayList<>();
        File folder = new File(MERGED_FILE_PATH);
        File[] files = folder.listFiles(new NameFilter2());
        if (files != null) {
            for (File f : files) {
                l.add(new Video(f.getAbsolutePath(), f.getName()));
            }
        }
        return l;
    }

    public static class NameFilter implements FilenameFilter {
        String pattern = "(\\d+)-(\\d+)-(\\d+).mp4";
        Pattern p = Pattern.compile(pattern);

        @Override
        public boolean accept(File dir, String filename) {
            Matcher matcher = p.matcher(filename);
            return matcher.find();
        }
    }

    public static class NameFilter2 implements FilenameFilter {
        String pattern = "(\\d+)-(\\d+)-(\\d+)到(\\d+)-(\\d+)-(\\d+).mp4";
        Pattern p = Pattern.compile(pattern);

        @Override
        public boolean accept(File dir, String filename) {
            Matcher matcher = p.matcher(filename);
            return matcher.find();
        }
    }

    //正则表达式http://www.runoob.com/java/java-regular-expressions.html
    public static VidDate getDateFromName(String name) {
        //仅限格式为2017-04-29.mp4这类文件
        String pattern = "(\\d+)-(\\d+)-(\\d+).mp4";
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(name);
        if (matcher.find()) {
            VidDate date = new VidDate();
            Log.i("TAG", "正则表达式解析结果:" + matcher.group(1) + "--" + matcher.group(2) + "--" + matcher.group(3));
            date.year = Integer.valueOf(matcher.group(1));
            date.month = Integer.valueOf(matcher.group(2));
            date.day = Integer.valueOf(matcher.group(3));
            return date;
        } else {
            return null;
        }

    }

    public static class VidDate {
        public int year;
        public int day;
        public int month;

        public VidDate() {
        }

        public VidDate(int y, int m, int d) {
            year = y;
            month = m;
            day = d;
        }

        public boolean isAfter(VidDate v) {
            //其实可以用文本操作实现，但是还是算了吧
            if (v.year == this.year) {
                if (v.month == this.month) {
                    if (v.day >= this.day) {
                        return false;
                    }
                    return true;
                }
                if (v.month > this.month) {
                    return false;
                }
                return true;
            }
            if (v.year > this.year) {
                return false;
            }
            return true;
        }
    }

    /**
     * 你需要确保path所在的目录存在
     */
    public static void mergeVideo(VidDate to, String path) {
        Log.e("TAG", "开始合并");
        List<Video> videoList = getVideoFileList();
        List<Video> suitableList = new ArrayList<>();
        //遍历适合的Video
        for (Video v : videoList) {
            if (!getDateFromName(v.name).isAfter(to)) {
                suitableList.add(v);
            }
        }
        //进行排序
        Log.e("TAG", "开始合并");
        //合并
        MP4Merge.Merge(suitableList, path);

        Log.e("TAG", "结束合并");
    }

    public static void addTimeMark(String path,String timeMark) {
        File f = new File(path + "timemark");
        if (f.exists()){
            f.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(timeMark);
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getTimeMark(String path){
        File f = new File(path + "timemark");
        if (f.exists()){
            FileInputStream fis = null;
            String result = "";
            try {
                fis = new FileInputStream(f);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                result = br.readLine();

                br.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return "";
    }

}
