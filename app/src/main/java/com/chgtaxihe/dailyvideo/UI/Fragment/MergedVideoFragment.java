package com.chgtaxihe.dailyvideo.UI.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.UI.Activity.RecordActivity;
import com.chgtaxihe.dailyvideo.UI.FragManager;
import com.chgtaxihe.dailyvideo.Util.MessageUtil;
import com.chgtaxihe.dailyvideo.VideoAdapter;

import java.util.List;

public class MergedVideoFragment extends Fragment {

    public final static String FRAGMENT_TAG = "merged_video";

    private View mView;
    private RecyclerView recycler_view;
    private List<Video> mVideos;

    private VideoAdapter mVideoAdapter;

    public MergedVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_merged_video, container, false);
        initView();
        return mView;
    }

    private void initView() {
        recycler_view = (RecyclerView) mView.findViewById(R.id.merged_recycle_view);
        mVideos = FileManager.getMergedVideos();
        recycler_view.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        VideoAdapter.OnClick onClick = new DefaultOnClick();//设置点击事件
        mVideoAdapter = new VideoAdapter(mVideos, onClick, null);
        recycler_view.setAdapter(mVideoAdapter);
        if (mVideos.isEmpty()) {
            MessageUtil.showDialogMessage(getActivity(),getString(R.string.on_no_merged_video),getString(R.string.btn_ok1));
            FragManager f = FragManager.getInstance();
            if (f.getFragmentManager() != null){
                if (f.isAdded(VideoFragment.FRAGMENT_TAG)){
                    f.show(VideoFragment.FRAGMENT_TAG);
                }else{
                    //Todo 这该怎么办
                }
            }
        }
    }


    /**
     * 默认点击事件
     */
    private class DefaultOnClick implements VideoAdapter.OnClick {
        @Override
        public void Click(View v, Video video) {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            android.app.FragmentTransaction tran = getFragmentManager().beginTransaction();
            Fragment f = getFragmentManager().findFragmentByTag("videoDialog");
            if (f != null) {
                tran.remove(f);
            }
            tran.addToBackStack(null);

            PlayVideoDialogFragment newFragment = new PlayVideoDialogFragment();
            Bundle b = new Bundle();
            b.putString("path", video.path);
            newFragment.setArguments(b);
            newFragment.show(tran, "videoDialog");
        }
    }
}
