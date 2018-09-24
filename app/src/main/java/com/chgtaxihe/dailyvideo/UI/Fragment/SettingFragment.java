package com.chgtaxihe.dailyvideo.UI.Fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chgtaxihe.dailyvideo.R;
import com.chgtaxihe.dailyvideo.Util.CacheManager;
import com.chgtaxihe.dailyvideo.Util.MessageUtil;

public class SettingFragment extends Fragment {

    public final static String FRAGMENT_TAG = "setting";

    private View mView;
    private TextView tv_cache_size;
    private RelativeLayout layout_delCache;
    private RelativeLayout author_word;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting, container, false);
        initView();
        return mView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            refreshTextView();
        }
    }

    private void initView() {
        tv_cache_size = (TextView) mView.findViewById(R.id.tv_cache_size);
        layout_delCache = (RelativeLayout) mView.findViewById(R.id.layout_delCache);
        author_word = (RelativeLayout) mView.findViewById(R.id.author_word);

        layout_delCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheManager.getInstance().cleanCache();
                tv_cache_size.setText("已用0M");
                makeToast("清理完毕");
            }
        });
        author_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageUtil.showDialogMessage(getActivity(),getString(R.string.author_word_all),getString(R.string.btn_ok3));
            }
        });

        refreshTextView();

    }

    private void refreshTextView(){
        float size = CacheManager.getInstance().getCacheSize()/1024;
        int i = (int)(size*100);
        size = i / 100f;
        //只取小数点后2位
        tv_cache_size.setText("已用" + size + "M");
    }

    private void makeToast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }


}
