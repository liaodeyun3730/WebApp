package com.web.webapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.web.deyunlib.SystemUtil;

public class MainActivity extends AppCompatActivity {
    TextView tv_hello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText(SystemUtil.getMobileInfo());
        SuperWebActivity.startActivity(this,"https://www.baidu.com");
        //这是master注释
    }
}
