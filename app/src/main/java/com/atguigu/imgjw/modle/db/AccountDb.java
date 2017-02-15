package com.atguigu.imgjw.modle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.atguigu.imgjw.modle.table.AccountTable;

/**
 * Created by 皇 上 on 2017/2/14.
 */

public class AccountDb extends SQLiteOpenHelper {

    public AccountDb(Context context) {
        super(context, "accound.db", null, 1);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(AccountTable.CREATE_TABLE);
    }

    //更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
