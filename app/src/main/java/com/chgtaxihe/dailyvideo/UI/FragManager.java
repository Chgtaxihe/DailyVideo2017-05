package com.chgtaxihe.dailyvideo.UI;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/29.
 */

public class FragManager {

    static final private String synTag = "CacheLock";
    private static FragManager manager;
    private static FragmentManager mFragmentManager;
    private static int mLayoutId;

    List<Fragment> mFragments = new ArrayList<>();

    private FragManager(){

    }
    public static FragManager getInstance(){
        if (manager == null){
            synchronized(synTag){
                if (manager == null){
                    manager = new FragManager();
                }
            }
        }
        return manager;
    }

    public void setFragmentManager(FragmentManager mFragmentManager) {
        FragManager.mFragmentManager = mFragmentManager;
    }

    public FragmentManager getFragmentManager() {
        return mFragmentManager;
    }

    public void setLayoutID(int id){
        mLayoutId = id;
    }

    public void add(Fragment fragment,String tag){
        FragmentTransaction tran = mFragmentManager.beginTransaction();
        if (!mFragments.isEmpty()){
            hideAll(tran);
        }
        tran.add(mLayoutId,fragment,tag);
        tran.commit();
        if (!mFragments.contains(fragment)){
            mFragments.add(fragment);
        }
    }

    public void show(String tag){
        FragmentTransaction tran = mFragmentManager.beginTransaction();
        hideAll(tran);
        tran.show(mFragmentManager.findFragmentByTag(tag));
        tran.commit();
    }

    public void hideAll(FragmentTransaction t){
        for (Fragment f : mFragments){
            t.hide(f);
        }
    }

    public boolean isAdded(String tag){
        if(mFragmentManager.findFragmentByTag(tag) == null){
            return false;
        }else {
            return true;
        }

    }

    /**
     * 若不能找到，则返回null
     */
    public Fragment getFragment(String tag){
        return mFragmentManager.findFragmentByTag(tag);
    }
}
