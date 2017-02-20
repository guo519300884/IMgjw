package com.atguigu.imgjw.modle;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.atguigu.imgjw.modle.bean.GroupInfo;
import com.atguigu.imgjw.modle.bean.InvitationInfo;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;

/**
 * Created by 皇 上 on 2017/2/15.
 */

public class GlobalListener {


    private final LocalBroadcastManager manager;

    public GlobalListener(Context context) {
        //注册联系人监听
        EMClient.getInstance().contactManager().setContactListener(listener);

        //注册群监听
        EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
        //本地广播 （本应用内收到 需要注册）
        manager = LocalBroadcastManager.getInstance(context);
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
            manager.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

        }

        //好友请求被同意  你加别人的时候 别人同意了
        @Override
        public void onContactAgreed(String username) {

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
            manager.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

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
            manager.sendBroadcast(new Intent(Contacts.CONTACT_CHAGED));

        }


        //增加了联系人时回调此方法  当你同意添加好友
        @Override
        public void onContactAdded(String username) {

            //添加用户
            Modle.getInstance().getDbManager().getContactDao()
                    .saveContacts(new UserInfo(username), true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.CONTACT_CHAGED));

        }

        //好友请求被拒绝  你加别人 别人拒绝了
        @Override
        public void onContactRefused(String username) {

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.NEW_INVITE_CHAGED));

        }
    };

    private final EMGroupChangeListener groupListener = new EMGroupChangeListener() {


        //收到加入群组的邀请  别的组邀请自己
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //添加邀请到数据库
            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            invitation.setGroupInfo(new GroupInfo(groupName, groupId, inviter));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);
            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }

        //群组邀请被拒绝  你邀请别人被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            invitationInfo.setGroupInfo(new GroupInfo(groupId, groupId, invitee));

            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitationInfo);
            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }

        //群组邀请被接受  你邀请别人 别人接受了
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {

            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            invitation.setGroupInfo(new GroupInfo(groupId, groupId, inviter));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }

        //收到加群申请   别人要加你的群
        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            invitation.setGroupInfo(new GroupInfo(groupName, groupId, applyer));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);
            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));

        }

        //加群申请被同意 你加别人的群 别人同意了
        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason("");
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            invitation.setGroupInfo(new GroupInfo(groupName, groupId, accepter));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }

        // 加群申请被拒绝  你加别人的群 别人拒绝了
        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason(reason);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            invitation.setGroupInfo(new GroupInfo(groupName, groupId, decliner));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }

        //当前用户被管理员移除出群组
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        //群组被创建者解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        //收到 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String invite, String inviteMessage) {

            InvitationInfo invitation = new InvitationInfo();
            invitation.setReason(inviteMessage);
            invitation.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            invitation.setGroupInfo(new GroupInfo(groupId, groupId, invite));
            Modle.getInstance().getDbManager().getInvitationDao()
                    .addInvitation(invitation);

            //显示小红点
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
            //发送广播
            manager.sendBroadcast(new Intent(Contacts.GROUP_INVITE_CHAGE));
        }
    };
}
