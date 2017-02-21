package com.atguigu.imgjw.modle;

import android.content.Context;

import com.atguigu.imgjw.modle.dao.AccountDao;
import com.atguigu.imgjw.modle.db.DBManager;
import com.atguigu.imgjw.utils.SpUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 皇 上 on 2017/2/14.
 */

public class Modle {

    /**
     * 1.私有构造器
     * 2.静态全局变量
     * 3.静态公共方法
     */

    private static Modle modle = new Modle();
    private Context context;
    private AccountDao accountDao;
    private DBManager dbManager;

    private Modle() {
    }

    public static Modle getInstance() {
        return modle;
    }

    public void init(Context context) {
        this.context = context;
        //创建AccountDao数据库
        accountDao = new AccountDao(context);
        //初始化全局监听
        new GlobalListener(context);
    }
     /*
    *
    * 线程池分为四种
    * 第一种 缓存线程池 有多少可以开启多少
    * 第二种 定长线程池  固定大小
    * 第三种 调度线程池  可以延时周期执行
    * 第四种  单例线程池  单个
    *
    */

    private ExecutorService thread = Executors.newCachedThreadPool();

    public ExecutorService getGlobalThread() {
        return thread;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public DBManager getDbManager() {
        return dbManager;
    }

    //登录成功后创建用户和邀请信息
    public void loginSuccess(String currenUser) {
        if (dbManager != null) {
            dbManager.close();
        }
        dbManager = new DBManager(context, currenUser + ".db");
    }


    public void ecitLogin() {
        SpUtils.getInstance().destory();
    }
}
