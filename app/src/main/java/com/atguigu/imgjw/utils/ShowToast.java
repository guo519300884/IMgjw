package com.atguigu.imgjw.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class ShowToast {
    public static void show(Activity activity, String context) {
        Toast.makeText(activity, context, Toast.LENGTH_SHORT).show();
    }

    public static void showUI(final Activity activity, final String context) {
        //进入分线程  activitty 有可能为空  须判断
        if (activity == null) {
            return;
        }

        // 回主线程中执行
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                show(activity, context);
            }
        });

    }


}
