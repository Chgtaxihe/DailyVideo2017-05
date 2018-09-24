package com.chgtaxihe.dailyvideo.File;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Administrator on 2017/4/2.
 */

/**
 * @url: http://blog.csdn.net/u012027644/article/details/53885837
 */
public class MP4Merge {

    public static void Merge(List<Video> list,String outputPath) {
        int count = list.size();

        try {
            Movie[] inMovie = new Movie[count];
            for (int i = 0; i < count; i++) {
                inMovie[i] = MovieCreator.build(list.get(i).path);
            }
            //为什么要用linkedList
            List<Track> videoTrack = new LinkedList<>();
            List<Track> soundTrack = new LinkedList<>();
            for(Movie movie:inMovie){
                for(Track track:movie.getTracks()){
                    if(track.getHandler().equals("soun")){
                        soundTrack.add(track);
                    }
                    if (track.getHandler().equals("vide")) {
                        videoTrack.add(track);
                    }
                    if (track.getHandler().equals("")) {

                    }
                }
            }

            //添加通道到新的视频里
            Movie result = new Movie();
            if (soundTrack.size() > 0) {
                result.addTrack(new AppendTrack(soundTrack
                        .toArray(new Track[soundTrack.size()])));
            }
            if (videoTrack.size() > 0) {
                result.addTrack(new AppendTrack(videoTrack
                        .toArray(new Track[videoTrack.size()])));
            }
            Container mp4file = new DefaultMp4Builder()
                    .build(result);
            //开始生产mp4文件
            File storagePath = new File(outputPath);
            FileOutputStream fos =  new FileOutputStream(storagePath);
            FileChannel fco = fos.getChannel();
            mp4file.writeContainer(fco);
            fco.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
