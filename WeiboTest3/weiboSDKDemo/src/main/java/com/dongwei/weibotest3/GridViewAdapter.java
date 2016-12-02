package com.dongwei.weibotest3;

import android.content.Context;
import android.util.Log;
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
 * Created by dongwei on 2016/11/15.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private ImageView imageView;
    private ImageView imageView2;
    private int imageId;
//    private ArrayList<ImageView> gridImageViews =new ArrayList<ImageView>();
    private ArrayList<String> gridImageViews = new ArrayList<String>();
    LayoutInflater mInflater;
    LinearLayout.LayoutParams params;

    public GridViewAdapter(Context context,int position,ArrayList<String>imageDatas) {

        this.mContext = context;
        this.imageId = position;
        if (imageDatas != null) {
            gridImageViews = imageDatas;
        }
        mInflater = LayoutInflater.from(context);
        Log.d("content===", String.valueOf(mContext));
        Log.d("imageDatas====", String.valueOf(imageDatas));
//        gridImageViews = mImageViews;
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
//        this.gridImageViews =mImageViews;
        notifyDataSetChanged();
//        Log.d("XXX", String.valueOf(gridImageViews));
    }


    @Override
    public int getCount() {
        return gridImageViews == null ? 0  :gridImageViews.size();

    }

    @Override
    public Object getItem(int position) {
        return gridImageViews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView==null){
//            LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
//            convertView = inflater.inflate(imageId,parent,false);
            LayoutInflater mInflater = LayoutInflater.from(mContext);
            convertView = mInflater.inflate(R.layout.grid_item,null);
            holder = new ViewHolder();
            holder.imageView= (ImageView)convertView.findViewById(R.id.grid_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

//        ImageView img =new ImageView(mContext);
        ImageLoader.getInstance().displayImage(gridImageViews.get(position).replace("thumbnail", "bmiddle"),holder.imageView);
//        Log.d("img===", String.valueOf(holder.imageView));
//        holder.imageView.setBackground(img.getDrawable());
        holder.imageView.setTag(gridImageViews.get(position));


        holder.imageView.setImageResource(R.drawable.ic_logo);
        if (holder.imageView.getTag() != null && holder.imageView.getTag().equals(gridImageViews.get(position))) {
           holder.imageView.setImageResource(R.drawable.ic_logo);
        }
        return convertView;
    }

    private class ViewHolder{
        public ImageView imageView ;

    }

}
