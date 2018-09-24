package com.chgtaxihe.dailyvideo.UI.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.Thread.LoadBitmapTask;
import com.chgtaxihe.dailyvideo.UI.Activity.MainActivity;
import com.chgtaxihe.dailyvideo.UI.Activity.RecordActivity;
import com.chgtaxihe.dailyvideo.UI.FragManager;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {

    public final static String FRAGMENT_TAG = "Main";

    private View mView;
    private LinearLayout layout_main;
    private CardView cardView;
    private Button btn_goRecord;

    private List<ImageView> mImages = new ArrayList<>();

    private boolean hasInited = false;

    public MainFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

//        initView();
        return mView;
    }

    private void initView() {
//        layout_main = (LinearLayout) mView.findViewById(R.id.layout_main);
//        cardView = (CardView) mView.findViewById(R.id.cardView);
//        btn_goRecord = (Button) mView.findViewById(R.id.btn_goRecord);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragManager manager = FragManager.getInstance();
                if (manager.getFragmentManager() == null){
                    manager.setFragmentManager(getFragmentManager());
                }
                if (manager.isAdded(VideoFragment.FRAGMENT_TAG)){
                    manager.show(VideoFragment.FRAGMENT_TAG);
                }else {
                    manager.add(new VideoFragment(),VideoFragment.FRAGMENT_TAG);
                }
            }
        });

        btn_goRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplication(), RecordActivity.class);
                startActivity(intent);
            }
        });

        //见:http://www.cnblogs.com/wt616/archive/2012/05/11/2496180.html
        ViewTreeObserver observer = layout_main.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!hasInited){
                    int width = layout_main.getWidth();
                    int height = layout_main.getHeight();
//                    width -= 15;
                    height -= 10;
                    Log.i(FRAGMENT_TAG,"宽:" + width + "  高:" + height);
                    List<Video> l = FileManager.getVideoFileList();
                    int size = l.size();
                    if (size != 0){
                        if (size >= 3) {
                            size = 3;
                        }
                        for (int i = 0;i < size;i++) {
                            ImageView imv = new ImageView(getActivity());
                            imv.setPadding(5, 5, 5, 5);

                            imv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            mImages.add(imv);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,height,1);
//                            params.weight = width/size;
                            LoadBitmapTask task = new LoadBitmapTask();
                            LoadBitmapTask.CLoader cl = new LoadBitmapTask.CLoader();
                            layout_main.addView(imv,params);
                            cl.imageView = imv;
                            cl.path = l.get(i).path;
                            task.execute(cl);
                        }
                    }else{
                        layout_main.setVisibility(View.INVISIBLE);
                    }
                    hasInited = true;
                }
                return true;
            }
        });


    }

}
