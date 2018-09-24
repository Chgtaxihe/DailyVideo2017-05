package com.chgtaxihe.dailyvideo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chgtaxihe.dailyvideo.File.Video;
import com.chgtaxihe.dailyvideo.Thread.LoadBitmapTask;

import java.util.List;

/**
 * Created by Administrator on 2017/4/1.
 */

/**
 * FooterView 应当同R.layout.video_item一致
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{

    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;



    private List<Video> mVideoList;
    private View mFooter = null;
    private OnClick mOnClick;

    public void setOnClick(OnClick OnClick) {
        this.mOnClick = OnClick;
    }

    public void setOnLongClick(OnLongClick OnLongClick) {
        this.mOnLongClick = OnLongClick;
    }

    private OnLongClick mOnLongClick;

    public VideoAdapter(List<Video> videoList){
        this(videoList,null,null);
    }

    public VideoAdapter(List<Video> videoList, OnClick onClickListener
            , OnLongClick onLongClickListener)
    {
        mVideoList = videoList;
        mOnClick = onClickListener;
        mOnLongClick = onLongClickListener;
    }

    public View getFooter() {
        return mFooter;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooter != null && position == getItemCount() -1){
            return TYPE_FOOTER;
        }else {
            return TYPE_NORMAL;
        }

    }

    //http://www.jianshu.com/p/991062d964cf
    public void setFooter(View mFooter) {
        this.mFooter = mFooter;
        notifyItemInserted(getItemCount()-1);
    }

    public void setVideoList(List<Video> list){
        mVideoList = list;
        //注意，这样子应该会导致已经加载的Footer消失
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_FOOTER && mFooter != null){
            return new ViewHolder(mFooter);
        }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
            VideoAdapter.ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
    }

    class clickListener implements View.OnClickListener{

        int mPos;

        clickListener(int pos){
            mPos = pos;
        }

        @Override
        public void onClick(View v) {
            mOnClick.Click(v, mVideoList.get(mPos));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if(type == TYPE_NORMAL) {

            if(mOnClick != null) {
                clickListener listener = new clickListener(position);
                holder.itemView.setOnClickListener(listener);
                holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        //设置触摸反馈
                        switch (action)
                        {
                            case MotionEvent.ACTION_DOWN:
                                v.setBackgroundColor(Color.LTGRAY);
                                break;
                            case MotionEvent.ACTION_UP:
                                Log.i("tag","action_up");
                                v.setBackgroundColor(Color.WHITE);
                                break;
                            case MotionEvent.ACTION_CANCEL:
                                Log.i("tag","ACTION_CANCEL");
                                v.setBackgroundColor(Color.WHITE);
                                break;
                        }
                        return false;
                    }
                });
            }
            if (mOnLongClick != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnLongClick.Click(v);
                        return true;
                    }
                });
            }
        }
        if (type == TYPE_FOOTER){
            holder.tv_title.setText("添加视频");
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageView.setImageResource(R.drawable.ic_add_video);
        }
    }

    @Override
    public int getItemCount() {
        if (mFooter == null) {
            return mVideoList.size();
        }else{
            return mVideoList.size()+1;
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView tv_title;
        View itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.cover_image);
            tv_title = (TextView) itemView.findViewById(R.id.description_tv);
        }
    }

    public interface OnClick{
        public void Click(View v,Video video);
    }
    public interface OnLongClick{
        public void Click(View v);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        int pos = holder.getAdapterPosition();
        if (holder.getItemViewType() == TYPE_FOOTER){
            return;
        }
        Video v = mVideoList.get(pos);
        //去掉 ".mp4"
        String s = v.name.substring(0,v.name.length()-4);
        holder.tv_title.setText(s);
        //异步加载图片
        //Todo 如果来回滑动的话，会产生多个Task，导致加载异常
        LoadBitmapTask task = new LoadBitmapTask();
        LoadBitmapTask.CLoader cl = new LoadBitmapTask.CLoader();
        cl.imageView = holder.imageView;
        cl.path = v.path;
        task.execute(cl);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        if (holder.getItemViewType() == TYPE_FOOTER){
            return;
        }
        BitmapDrawable drawable = (BitmapDrawable)holder.imageView.getDrawable();
        if (drawable != null) {
            Bitmap bmp = drawable.getBitmap();
            if (bmp != null) {
                bmp.recycle();
            }
        }
        holder.imageView.setImageBitmap(null);
        holder.imageView.setBackgroundColor(Color.WHITE);
    }

}
