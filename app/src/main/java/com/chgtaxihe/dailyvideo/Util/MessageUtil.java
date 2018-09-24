package com.chgtaxihe.dailyvideo.Util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
//使用V7的包，保证MD风格

/**
 * Created by Administrator on 2017/5/13.
 */

public class MessageUtil {

    public static void showDialogMessage(Context context,String msg,String btnMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton(btnMsg,null)
                .show();
    }

}
