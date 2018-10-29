package com.web.webapp;

import android.app.Application;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FakeX509TrustManager.allowAllSSL();//生产环境是ssl的，有的手机不配这个，https的图片也显示不了
    }
}
