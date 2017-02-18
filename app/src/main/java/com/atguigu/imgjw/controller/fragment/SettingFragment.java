package com.atguigu.imgjw.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.activity.LoginActivity;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class SettingFragment extends Fragment {
    @InjectView(R.id.settings_btn_logout)
    Button settingsBtnLogout;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //进入服务器
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            //在分线程中进行
            @Override
            public void run() {
                //在服务器获取用户名
                String currentUser = EMClient.getInstance().getCurrentUser();
                //设置button的信息
                settingsBtnLogout.setText("退出登录(" + currentUser + ")");
            }
        });
    }

    @OnClick(R.id.settings_btn_logout)
    public void onClick() {
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                //在服务器端退出
                EMClient.getInstance().logout(false, new EMCallBack() {
                    //退出成功
                    @Override
                    public void onSuccess() {

                        Modle.getInstance().ecitLogin();
                        //判断此页面是否为空
                        if (getActivity() == null) {
                            return;
                        }
                        //跳转回登录页面
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        ShowToast.showUI(getActivity(), "退出来了");
                        //关闭此页面
                        getActivity().finish();
                    }

                    // 退出失败
                    @Override
                    public void onError(int i, String s) {
                        ShowToast.showUI(getActivity(), "退不出来了嘿嘿嘿" + s);

                    }

                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
