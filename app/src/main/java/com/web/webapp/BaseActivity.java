package com.web.webapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public abstract class BaseActivity extends FragmentActivity {
    protected BaseActivity mThis;

    protected <T> T $(int viewID) {
        return (T) findViewById(viewID);
    }

    protected boolean selfStatusBaColor;
    protected boolean noNeedEventBus;//不需要EventBus;改变这个值需要在调用super.onCreate之前调用
    protected boolean saveInstance;//是否保存状态，默认不保存

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        if (!selfStatusBaColor) {
            UIUtil.setStatusBarColor(this);
        }

        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId());
            initView();
            initData();
            bindEvent();
        }

    }


    protected abstract int getLayoutResId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void bindEvent();




    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }





    @Override
    protected void onResume() {
        super.onResume();//注：回调 1

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }




    private static long lastClickTime;
    protected boolean enableFastClick;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 300) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //默认不用保存状态，只是不保存Fragment和一些控件状态,但是重启acitivity是的Intent参数还是会传过来
        if(saveInstance) {
            super.onSaveInstanceState(outState);
        }
    }
}
