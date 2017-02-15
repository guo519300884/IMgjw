package com.atguigu.imgjw.modle.db;

import android.content.Context;

import com.atguigu.imgjw.modle.dao.ContactDao;
import com.atguigu.imgjw.modle.dao.InvitationDao;

/**
 * Created by 皇 上 on 2017/2/15.
 */

public class DBManager {

    private final ContactDao contactDao;
    private final InvitationDao invitationDao;
    private final DBHelper dbHelper;

    public DBManager(Context context, String name) {
        dbHelper = new DBHelper(context, name);
        //创建联系人的操作类
        contactDao = new ContactDao(dbHelper);
        //创建邀请信息操作类
        invitationDao = new InvitationDao(dbHelper);

    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public InvitationDao getInvitationDao() {
        return invitationDao;
    }

    public void close() {
        dbHelper.close();
    }
}
