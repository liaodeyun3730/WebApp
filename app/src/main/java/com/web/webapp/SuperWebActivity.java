package com.web.webapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


public class SuperWebActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_TITLE = "title";
    private WebView webView;
    ImageView iv_close;
    TextView tv_title;
    ImageButton ib_web_refresh, ib_go_forword, ib_go_back;
    Bundle savedInstanceState;
    private String mTitle;
    WebChromeClient wcc = null;
    WebViewClient wvc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        noNeedEventBus = true;
        this.savedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_super_web;
    }

    @Override
    public void initView() {
        webView = $(R.id.webView);
        ib_web_refresh = $(R.id.ib_web_refresh);
        ib_go_forword = $(R.id.ib_go_forword);
        ib_go_back = $(R.id.ib_go_back);
        iv_close = $(R.id.iv_close);
        tv_title = $(R.id.tv_title);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initData() {
        // TODO Auto-generated method stub
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);
        if(!TextUtils.isEmpty(mTitle)) {
            tv_title.setText(mTitle);
        }
        wvc = new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        };

        wcc = new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                ib_go_forword.setEnabled(webView.canGoForward());
                ib_go_back.setEnabled(webView.canGoBack());
                if (newProgress == 100) {
                    ib_web_refresh.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(mTitle))
                        tv_title.setText(view.getTitle());
                } else {
                    ib_web_refresh.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }
        };
        webView.setWebViewClient(wvc);
        webView.setWebChromeClient(wcc);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setHorizontalScrollBarEnabled(true);
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            Intent intent = getIntent();
            if (intent != null && intent.getData() != null) {
                String url = intent.getData().toString();
                webView.loadUrl(url);
            } else {
                finish();
            }
        }

    }

    @Override
    public void bindEvent() {
        // TODO Auto-generated method stub
        ib_web_refresh.setOnClickListener(this);
        ib_go_forword.setOnClickListener(this);
        ib_go_back.setOnClickListener(this);
        iv_close.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        destroyWebView();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    protected void destroyWebView() {
        if (wvc != null) {
            webView.setWebViewClient(null);
            wvc = null;
        }
        if (wcc != null) {
            webView.setWebChromeClient(null);
            wvc = null;
        }
        webView.pauseTimers();
        webView.removeAllViews();
        webView.destroy();
        webView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.ib_web_refresh:
                ib_web_refresh.setVisibility(View.GONE);
                webView.reload();
                break;
            case R.id.ib_go_back:
                if (webView.canGoBack())
                    webView.goBack();
                break;
            case R.id.ib_go_forword:
                if (webView.canGoForward())
                    webView.goForward();
                break;
        }
    }

    public static void startActivity(Context context, String url) {
        startActivity(context, url, null);
    }

    public static void startActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, SuperWebActivity.class);
        intent.setData(Uri.parse(url));
        if (!TextUtils.isEmpty(title))
            intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }
}
