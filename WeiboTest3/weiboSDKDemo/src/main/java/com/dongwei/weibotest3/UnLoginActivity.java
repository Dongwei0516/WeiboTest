package com.dongwei.weibotest3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dongwei.weibotest3.utils.AccessTokenKeeper;
import com.dongwei.weibotest3.utils.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Created by dongwei on 2016/11/9.
 */

public class UnLoginActivity extends Activity{

    private static final String TAG = "weibosdk";

    private TextView mTokenText;
    private AuthInfo mAutoInfo;
    private Oauth2AccessToken mAccessToken;
    protected SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unlogin_activity);

        mAutoInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(this, mAutoInfo);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSsoHandler.authorize(new AuthListener());
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSsoHandler.registerOrLoginByMobile("手机验证", new AuthListener());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

    }

    class AuthListener implements WeiboAuthListener{
        @Override
        public void onComplete(Bundle values){
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()){
                AccessTokenKeeper.writeAccessToken(UnLoginActivity.this, mAccessToken);
                Toast.makeText(UnLoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UnLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)){
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(UnLoginActivity.this, message , Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UnLoginActivity.this,
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(UnLoginActivity.this,"取消授权", Toast.LENGTH_SHORT ).show();
        }
    }


}
