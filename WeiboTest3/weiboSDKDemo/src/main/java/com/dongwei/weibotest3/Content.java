package com.dongwei.weibotest3;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by dongwei on 2016/11/10.
 */

public class Content {


    public static void headContent(Context context, Status status, ImageView profile_img, TextView profile_name, TextView profile_time){
        setWeiBoImg(context , status.user, profile_img);
        setWeiBoName(profile_name, status.user);
        setWeiBoTime(context, profile_time, status);
    }

    public static void setWeiBoImg(Context context, User user, ImageView profile_img){
        ImageLoader.getInstance().displayImage(user.avatar_hd, profile_img);
    }

    public static void setWeiBoName(TextView textView, User user) {
        if (user.remark != null && user.remark.length() > 0) {
            textView.setText(user.remark);
        } else {
            textView.setText(user.name);
        }
    }

    public static void setWeiBoTime(Context context, TextView textView, Status status) {
        textView.setText(status.created_at);

    }
}
