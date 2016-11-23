package com.dongwei.weibotest3;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by dongwei on 2016/11/22.
 */

public class NineGridView extends GridView {
    public NineGridView(Context context) {
        super(context);
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
        }
}
