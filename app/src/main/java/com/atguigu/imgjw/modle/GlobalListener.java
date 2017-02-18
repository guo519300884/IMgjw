package com.atguigu.imgjw.modle;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.atguigu.imgjw.modle.bean.InvitationInfo;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by 皇 上 on 2017/2/15.
 */

public class GlobalListener {


    private final LocalBroadcastManager lm;

    public GlobalListener(Context context) {
        //注册联系人监听
        EMClient.getInstance().contactManager().setContactListener(listener);
        //本地广播 （本应用内收到 需要注册）
        lm = LocalBroadcastManager.getInstance(context);
    }

    EMContactListener listener = new EMContactListener() {

        //收到好友邀请  别人加你
        @Override
        public void onContactInvited(String username, String reason) {
            /**
             *  收到邀请后的逻辑
             * 1.添加邀请的信息
             * 2.显示红点
             * 3.发送广播
             */

            //添加邀请信息
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(username));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);
            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            lm.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

        }

        //好友请求被同意  你加别人的时候 别人同意了
        @Override
        public void onContactAgreed(String username) {

            //添加进联系人列表
//            Modle.getInstance().getDbManager().getContactDao()
//                    .saveContacts(new UserInfo(username), true);
            //添加邀请信息
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUserInfo(new UserInfo(username));
            invitationInfo.setReason("邀请被接受");
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            lm.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

        }

        //被删除时回调此方法
        @Override
        public void onContactDeleted(String username) {

            //移除用户信息
            Modle.getInstance().getDbManager().getContactDao()
                    .deleteContactByHxId(username);

            //移除邀请信息
            Modle.getInstance().getDbManager().getInvitationDao()
                    .removeInvitation(username);

            //显示小红点
//            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            lm.sendBroadcast(new Intent(Contacts.CONTACT_CHAGED));

        }


        //增加了联系人时回调此方法  当你同意添加好友
        @Override
        public void onContactAdded(String username) {

            //添加用户
            Modle.getInstance().getDbManager().getContactDao()
                    .saveContacts(new UserInfo(username), true);
            //发送广播
            lm.sendBroadcast(new Intent(Contacts.CONTACT_CHAGED));

        }

        //好友请求被拒绝  你加别人 别人拒绝了
        @Override
        public void onContactRefused(String username) {

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            lm.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

        }
    };
}
