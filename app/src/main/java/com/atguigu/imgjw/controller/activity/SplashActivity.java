package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.Modle;
import com.hyphenate.chat.EMClient;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //进入下一页面（主页或者登录页面）
                enterMainOrLogin();

            }

        }
    };

    private void enterMainOrLogin() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //判断是否登录了
                boolean loggedInBefore = EMClient.getInstance().isLoggedInBefore();
                if (loggedInBefore) {
                    //登录成功后需要处理的
                    Modle.getInstance().loginSuccess(EMClient.getInstance().getCurrentUser());
                    // 登录过的 跳转主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    //结束当前页面
                    finish();
                } else {
                    //没有登陆过

                    //没登陆过 跳转登录页面
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    //结束此页面
                    finish();
                }

            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //发送延时消息
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除所有消息
        handler.removeCallbacksAndMessages(null);
    }
}
