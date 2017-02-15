package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.login_et_username)
    EditText loginEtUsername;
    @InjectView(R.id.login_et_password)
    EditText loginEtPassword;
    @InjectView(R.id.login_btn_register)
    Button loginBtnRegister;
    @InjectView(R.id.login_btn_login)
    Button loginBtnLogin;
    private String username;
    private String passwrod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
    }

    //验证账号密码是否为空
    private boolean validate() {

        username = loginEtUsername.getText().toString().trim();
        passwrod = loginEtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(passwrod)) {
            ShowToast.show(this, "账号或密码为空");
            return false;
        }
        return true;
    }

    @OnClick({R.id.login_btn_register, R.id.login_btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_register:
                if (validate()) {
                    Modle.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            //进服务区注册
                            try {
                                EMClient.getInstance().createAccount(username, passwrod);
                                ShowToast.showUI(LoginActivity.this, "注册成功");
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ShowToast.showUI(LoginActivity.this, "注册失败" + e.getMessage());
//                                Toast.makeText(LoginActivity.this, "没成", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                break;
            case R.id.login_btn_login:
                if (validate()) {
                    Modle.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            EMClient.getInstance().login(username, passwrod, new EMCallBack() {
                                //登录成功
                                @Override
                                public void onSuccess() {
                                    //登录成后需要理
                                    Modle.getInstance()
                                            .loginSuccess(EMClient
                                                    .getInstance()
                                                    .getCurrentUser());
                                    //将账号保存进数库
                                    Modle.getInstance()
                                            .getAccountDao()
                                            .addAccount(
                                                    new UserInfo(EMClient
                                                            .getInstance()
                                                            .getCurrentUser()));
                                    //进入下一页面
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    ShowToast.showUI(LoginActivity.this, "登录成功");
                                    //关闭此页面
                                    finish();
                                }

                                //登录失败
                                @Override
                                public void onError(int i, String s) {
                                    ShowToast.showUI(LoginActivity.this, "登录失败" + s);
                                }

                                @Override
                                public void onProgress(int i, String s) {

                                }
                            });
                        }
                    });
                }
                break;
        }
    }
}
