package com.atguigu.imgjw;

import android.app.Application;

import com.atguigu.imgjw.modle.Modle;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

/**
 * Created by 皇 上 on 2017/2/14.
 */

public class ImApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        initHXSdk();
        //初始化Modle
        Modle.getInstance().init(this);
    }

    private void initHXSdk() {
        //配置文件
        EMOptions options = new EMOptions();
        //总接受邀请
        options.setAcceptInvitationAlways(false);
        //自动接受群邀请
        options.setAutoAcceptGroupInvitation(false);
        //初始化 EaseUI
        EaseUI.getInstance().init(this, options);

    }
}
