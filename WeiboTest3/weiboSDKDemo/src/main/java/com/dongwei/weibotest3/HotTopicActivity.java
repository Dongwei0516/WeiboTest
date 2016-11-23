package com.dongwei.weibotest3;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import static com.dongwei.weibotest3.R.id.webview;

/**
 * Created by dongwei on 2016/11/17.
 */

public class HotTopicActivity extends Activity {
    private ImageView mBack;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotweibo_item);

        mBack =(ImageView)findViewById(R.id.hotweibo_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mWebView = new WebView(this);
        mWebView = (WebView)findViewById(webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://m.weibo.cn/p/index?containerid=100803");
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

    }
}
