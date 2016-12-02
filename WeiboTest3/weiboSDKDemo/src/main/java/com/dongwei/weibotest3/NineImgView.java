package com.dongwei.weibotest3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;

/**
 * Created by dongwei on 2016/11/28.
 */

public class NineImgView extends ViewGroup{

    public final static int LINE_MAX_COUNT = 3;
    public int mLineMaxCount = 3;
    private int mPicSpace = 5; //图片间距
    private int mChildSize;
    private int mChildViewCount;
    private int mMaxChildCount = 9;
    private ArrayList<String> imageDatas;
    private int mImgCount;
    private static final int maxPicSize = 80;
    private int mWidth;
    private int mHeight;
    private ClickCallback mClickCallback;

    private ImageLoader mLoader = ImageLoader.getInstance();

    public NineImgView(Context context) {
        super(context);
    }

    public NineImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NineImgView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImgs(ArrayList mImgs, int len){
        dealWithImgs(mImgs);
    }

    public int getImgLines(int imgSize){
        if (imgSize == 0){
            return 0;
        }
        return (imgSize + mLineMaxCount -1)/mLineMaxCount;
    }


    public interface ClickCallback{
//        void callback(int index,String url);
        void callback(int index, ArrayList<String> arrayList);
    }

    public void setClickCallback(ClickCallback callback){
        mClickCallback = callback;
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof ImageView){
                int index = (Integer)v.getTag();
                if (mClickCallback != null){
                    mClickCallback.callback(index, imageDatas);
                }
            }
        }
    };

    protected void measureImgWidth(int widthMeasureSpec){
        if (mChildSize == 0){
            int measureSize = MeasureSpec.getSize(widthMeasureSpec);
            mChildSize = (measureSize - (LINE_MAX_COUNT -1)*mPicSpace - getPaddingLeft() - getPaddingRight())/LINE_MAX_COUNT;
        }
    }

    public void setMaxChildCount(int len){
        removeAllViews();
        mMaxChildCount = len;
        for (int i =0; i <len; i++){
            ImageView imageView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setTag(i);
            imageView.setOnClickListener(mClickListener);
            this.addView(imageView, params);
        }
    }

    private int getVisibleChildCount(){
        int childCount = getChildCount();
        int count = 0;
        for (int i = 0; i < childCount; i++){
            if (getChildAt(i).getVisibility() != View.GONE){
                count++;
            }
        }
        return count;
    }

    @Override
    protected void onMeasure(int widthMeasureSepc, int heightMeasureSpec){
        if (getChildCount()==0){
            setMaxChildCount(mMaxChildCount);
//            setImgs(imageDatas,mMaxChildCount);
        }
        measureImgWidth(widthMeasureSepc);
        mChildViewCount = getVisibleChildCount();
        int lines = getImgLines(mChildViewCount);
        int viewHeight = ((lines - 1) * mPicSpace + lines * mChildSize) + getPaddingTop() + getPaddingBottom();
        if (mChildViewCount ==1){
            viewHeight =mHeight == 0 ? viewHeight : mHeight;
        }
        int widthSize = MeasureSpec.getSize(widthMeasureSepc);
        setMeasuredDimension(widthSize , viewHeight);
        int heightSize = mChildSize;
        widthSize = heightSize;
        if (mChildViewCount == 1 && mWidth != 0){
            widthSize = mWidth;
            heightSize = mHeight;
        }
        measureChildren(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        int left = leftPadding, top = topPadding;
        int childCount = getChildCount();
        int visibleChildCount = mChildViewCount;
        int breakLine = 0;
        if (visibleChildCount == 4){
            breakLine = 2;
        }else {
            breakLine = mLineMaxCount;
        }
        for (int i =0; i < childCount; i++){
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE){
                continue;
            }
            if (visibleChildCount == 1){
                if (mLineMaxCount == 1){
                    left = (getMeasuredWidth() - mWidth)/2;
                }
                if (mWidth == 0 || mHeight == 0){
                    child.layout(left , top, left+mChildSize, top+mChildSize);
                }else {
                    child.layout(left , top, left+mWidth, top+mHeight);
                }
            }else {
                child.layout(left, top, left+mChildSize, top+mChildSize);
                left +=(mPicSpace + mChildSize);
                if ((i+1)%breakLine == 0){
                    top += mChildSize + mPicSpace;
                    left = leftPadding;
                }
            }
        }
    }

    private void dealWithImgs(ArrayList imgs){
        if (imgs ==null || imgs.size() == 0){
            return;
        }
        imageDatas = imgs;
        mImgCount = imgs.size();
        int imgLen = mImgCount;
        int maxChildCount = mMaxChildCount;
        for (int i =0; i<maxChildCount; i++){
            final ImageView mImageView = (ImageView)getChildAt(i);
            if (i<imgLen){
                mImageView.setVisibility(View.VISIBLE);
                String url = String.valueOf(imgs.get(i));
                ImageSize imageSize = null;
                if (i == 0 && mImgCount ==1 && mWidth != 0 && mHeight != 0){
                    imageSize = new ImageSize(mWidth, mHeight);
                }else {
                    imageSize = new ImageSize(maxPicSize, maxPicSize);
                }
                loadImg(url, imageSize, mImageView);
            }else {
                mImageView.setVisibility(View.GONE);
            }
        }
    }

    private void loadImg(String url, ImageSize imageSize, final ImageView mImageView){
        mLoader.displayImage(url,mImageView);
    }
}
