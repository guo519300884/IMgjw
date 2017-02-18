package com.atguigu.imgjw.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.atguigu.imgjw.ImApplication;
import com.hyphenate.chat.EMClient;

/**
 * Created by 皇 上 on 2017/2/16.
 */

public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite";// 新的邀请标记
    private static SpUtils instance = new SpUtils();
    private static SharedPreferences mSp;


    public static SpUtils getInstance() {
        if (mSp == null) {
            mSp = ImApplication.getContext()
                    .getSharedPreferences(EMClient.getInstance()
                            .getCurrentUser(), Context.MODE_PRIVATE);
        }
        return instance;
    }

    public void destory() {
        mSp = null;
    }

    // 保存
    public void save(String key, Object value) {

        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }

    // 获取数据的方法 获取String类型的数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 获取boolean数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 获取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }
}
