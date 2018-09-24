package com.chgtaxihe.dailyvideo.UI.Fragment;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.chgtaxihe.dailyvideo.R;

import java.io.File;

/**
 * Created by Administrator on 2017/4/2.
 */

public class PlayVideoDialogFragment extends DialogFragment implements MediaPlayer.OnCompletionListener{

    private String mPath;
    private VideoView videoView;
    private ImageView imageView;
    //Todo 用户关闭Fragment时，需要关闭input
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        mPath = b.getString("path");
        if (mPath == null)
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View layout = inflater.inflate(R.layout.dialogfragment_playvideo,null);
        builder.setView(layout);
        videoView = (VideoView) layout.findViewById(R.id.dialog_video_player);
        videoView.setVideoPath(mPath);
        videoView.setOnCompletionListener(this);

        imageView = (ImageView) layout.findViewById(R.id.dialog_image_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        this.dismiss();
    }
}
