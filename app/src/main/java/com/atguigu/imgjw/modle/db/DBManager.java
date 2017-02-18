package com.atguigu.imgjw.modle.db;

import android.content.Context;
import android.util.Log;

import com.atguigu.imgjw.modle.dao.ContactDao;
import com.atguigu.imgjw.modle.dao.InvitationDao;
import com.atguigu.imgjw.modle.table.ContactTable;
import com.atguigu.imgjw.modle.table.InvitationTable;

/**
 * Created by 皇 上 on 2017/2/15.
 */

public class DBManager {

    private final ContactDao contactDao;
    private final InvitationDao invitationDao;
    private final DBHelper dbHelper;
    private InvitationTable invitationTable;
    private ContactTable contactTable;

    public DBManager(Context context, String name) {
        dbHelper = new DBHelper(context, name);
        Log.e("TAG", "DBManager DBManager()" + dbHelper.toString());
        //创建联系人的操作类
        contactDao = new ContactDao(dbHelper);
        //创建邀请信息操作类
        invitationDao = new InvitationDao(dbHelper);

    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public InvitationDao getInvitationDao() {
        Log.e("TAG", "DBManager getInvitationDao()");
        return invitationDao;
    }

    public void close() {
        dbHelper.close();
    }


}
