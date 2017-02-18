package com.atguigu.imgjw.modle.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.modle.db.DBHelper;
import com.atguigu.imgjw.modle.table.ContactTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 皇 上 on 2017/2/15.
 */

public class ContactDao {
    private DBHelper dbHelper;

    public ContactDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    //获取所有联系人
    public List<UserInfo> getContacts() {
        //连接服务器
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //查询
        String sql = "select * from" + ContactTable.TABLE_NAME
                + " where " + ContactTable.COL_IS_CONTACT + "=1";
        Cursor cursor = database.rawQuery(sql, null);
        List<UserInfo> userInfos = new ArrayList<>();

        //封装进数据
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_HXID)));
            userInfo.setUsername(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_PHOTO)));
            userInfos.add(userInfo);
        }
        //关闭游标
        cursor.close();
        return userInfos;
    }

    //通过环信id获取单个用户的信息
    public UserInfo getContactByHx(String hxId) {

        //1.校验
        if (TextUtils.isEmpty(hxId)) {
            return null;
        }
        //2.获取连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String sql = " select * from" + ContactTable.TABLE_NAME
                + " where " + ContactTable.COL_USER_HXID + "=?";
        Cursor cursor = database.rawQuery(sql, new String[]{hxId});

        UserInfo userInfo = null;
        while (cursor.moveToNext()) {
            userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_HXID)));
            userInfo.setUsername(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NAME)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_PHOTO)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_USER_NICK)));
        }
        //3.关闭游标
        cursor.close();
        return userInfo;
    }


    //通过环信id获取所有用户信息
    public List<UserInfo> getContactByHx(List<String> hxIds) {

        //校验
        if (hxIds == null || hxIds.size() == 0) {
            return null;
        }
        //封装数据
        List<UserInfo> userInfos = new ArrayList<>();
        for (String hxId : hxIds) {
            UserInfo userInfo = getContactByHx(hxId);
            if (userInfo != null) {
                userInfos.add(userInfo);
            }
        }
        return userInfos;
    }

    //保存单个用户
    public void saveContacts(UserInfo user, boolean isMyContact) {
        //校验
        if (user == null) {
            return;
        }
        //获取链接
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContactTable.COL_USER_HXID, user.getHxid());
        contentValues.put(ContactTable.COL_USER_NAME, user.getUsername());
        contentValues.put(ContactTable.COL_USER_NICK, user.getNick());
        contentValues.put(ContactTable.COL_USER_PHOTO, user.getPhoto());
        contentValues.put(ContactTable.COL_IS_CONTACT, isMyContact ? 1 : 0);
        database.replace(ContactTable.TABLE_NAME, null, contentValues);

    }

    //保存所有联系人
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {

        if (contacts == null || contacts.size() == 0) {
            return;
        }
        for (UserInfo userinfo : contacts) {
            saveContacts(userinfo, isMyContact);
        }

    }

    //删除联系人的信息
    public void deleteContactByHxId(String hxId) {
        //校验
        if (TextUtils.isEmpty(hxId)) {
            return;
        }
        //获取连接
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        database.delete(ContactTable.TABLE_NAME,
                ContactTable.COL_USER_HXID + "=?",
                new String[]{hxId});

    }
}
