package com.dongwei.weibotest3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dongwei.weibotest3.utils.AccessTokenKeeper;
import com.dongwei.weibotest3.utils.Constants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

/**
 * Created by dongwei on 2016/11/16.
 */

public class PostNewActivity extends Activity {

    private UsersAPI mUsersAPI;
    private Status mStates;
    private com.sina.weibo.sdk.openapi.StatusesAPI mStatusesAPI;
    private Context mContext;
    private TextView mCancelButton;
    private TextView mSendButton;
    private EditText mEditText;

    private static final int TEXT_LIMIT = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_item);
        mContext = this;
        mStatusesAPI = new StatusesAPI(this, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(this));
        mUsersAPI = new UsersAPI(mContext, Constants.APP_KEY, AccessTokenKeeper.readAccessToken(this));
        mCancelButton = (TextView)findViewById(R.id.post_cancel);
        mSendButton = (TextView)findViewById(R.id.post_send);
        mEditText = (EditText)findViewById(R.id.post_content);

        setUpListener();
        mEditText.setTag(false);
    }

    private void setUpListener(){
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mEditText.getText().toString().isEmpty() || mEditText.getText().toString().length() == 0)) {
                    Toast.makeText(PostNewActivity.this,"发送内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (calculateWeiboLength(mEditText.getText().toString())>TEXT_LIMIT){
                    Toast.makeText(PostNewActivity.this,"文本超出限制",Toast.LENGTH_SHORT).show();
                    return;
                }
                mStatusesAPI.update(mEditText.getText().toString(),null, null, mListener);
                finish();
            }

        });

    }

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("{\"statuses\"")) {
                    // 调用 StatusList#parse 解析字符串成微博列表对象
                    StatusList statuses = StatusList.parse(response);
                    if (statuses != null && statuses.total_number > 0) {
                        Toast.makeText(PostNewActivity.this,
                                "获取微博信息流成功, 条数: " + statuses.statusList.size(),
                                Toast.LENGTH_LONG).show();
                    }
                } else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    Status status = Status.parse(response);
                    Toast.makeText(PostNewActivity.this,
                            "发送一送微博成功, id = " + status.id,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PostNewActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(PostNewActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public long calculateWeiboLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int temp = (int) c.charAt(i);
            if (temp > 0 && temp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }
}
