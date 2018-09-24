package com.chgtaxihe.dailyvideo.UI.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chgtaxihe.dailyvideo.File.FileManager;
import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.UI.Activity.RecordActivity;
import com.chgtaxihe.dailyvideo.VideoAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/4/29.
 */

public class VideoFragment extends Fragment {

    public final static String FRAGMENT_TAG = "Video";

    private RecyclerView recycler_view;
    private List<Video> mVideoList;
    private VideoAdapter mVideoAdapter;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_video, container, false);
        initView();
        return mView;
    }

    public void refreshRecycleView() {
        mVideoList = FileManager.getVideoFileList();
        mVideoAdapter.setVideoList(mVideoList);
    }

    private void initView() {
        recycler_view = (RecyclerView) mView.findViewById(R.id.recycler_view);
        /**
         * 设置recycle_view
         */
        //第一次见这样的new的方法
        mVideoList = FileManager.getVideoFileList();
        //恶心，LayourManager必须设置
        //http://stackoverflow.com/questions/28115553/recyclerview-not-call-any-adapter-method-oncreateviewholder-onbindviewholder
        recycler_view.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        VideoAdapter.OnClick onClick = new DefaultOnClick();//设置点击事件
        mVideoAdapter = new VideoAdapter(mVideoList, onClick, null);
        if (mVideoList.isEmpty()) {
            //如果一个视频也没有，那么就添加个footer提醒
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.video_item, recycler_view, false);
            mVideoAdapter.setFooter(view);
            //注意：Footer的TextView/ImageView里的内容被写到VideoAdapter里去了
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplication(), RecordActivity.class);
                    startActivity(intent);
                }
            });
        }
        recycler_view.setAdapter(mVideoAdapter);
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


    private void makeToast(String str) {
        Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
    }

}
