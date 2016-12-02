package com.dongwei.weibotest3;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by dongwei on 2016/11/28.
 */

public class NineImgAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> nineImageViews = new ArrayList<String>();
    LayoutInflater mInflater;
    private int imageId;
    LinearLayout.LayoutParams params;

    public NineImgAdapter(Context context, int position, ArrayList<String>imageDatas){
        this.mContext = context;
        this.imageId = position;
        if (imageDatas != null) {
            nineImageViews = imageDatas;
        }
        mInflater = LayoutInflater.from(context);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return nineImageViews.size();
    }

    @Override
    public Object getItem(int position) {
        return nineImageViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null){
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.grid_item,null);
            holder = new ViewHolder();
            holder.imageView= (ImageView)convertView.findViewById(R.id.grid_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(nineImageViews.get(position).replace("thumbnail", "bmiddle"),holder.imageView);
        holder.imageView.setTag(nineImageViews.get(position));

        holder.imageView.setImageResource(R.drawable.ic_logo);
        if (holder.imageView.getTag() != null && holder.imageView.getTag().equals(nineImageViews.get(position))) {
            holder.imageView.setImageResource(R.drawable.ic_logo);
        }
        return convertView;
    }

    private class ViewHolder{
        public ImageView imageView ;

    }

}
