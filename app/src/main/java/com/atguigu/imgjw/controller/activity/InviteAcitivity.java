package com.atguigu.imgjw.controller.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.atguigu.imgjw.ImApplication;
import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.adapter.InviteAdapter;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.modle.bean.InvitationInfo;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class InviteAcitivity extends AppCompatActivity {

    @InjectView(R.id.lv_invite)
    ListView lvInvite;
    private InviteAdapter adapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };
    private LocalBroadcastManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.inject(this);

        initView();
        initData();

    }

    private void initData() {
//        manager = LocalBroadcastManager.getInstance(this);
        manager = LocalBroadcastManager.getInstance(ImApplication.getContext());
        manager.registerReceiver(receiver, new IntentFilter(Contacts.NEW_INVITE_CHAGED));
    }

    private void initView() {
        adapter = new InviteAdapter(this, new InviteAdapter.OnInviteChangeListener() {

            //接受添加联系人
            @Override
            public void onAccept(final InvitationInfo info) {
                //点击接受
                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //通知网络环信服务器
                            EMClient.getInstance()
                                    .contactManager()
                                    .acceptInvitation(info.getUserInfo().getHxid());
                            //本地添加
                            Modle.getInstance().getDbManager()
                                    .getInvitationDao()
                                    .updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT,
                                            info.getUserInfo().getHxid());
                            //保存到内存并显示在页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "添加上了，开始聊骚了");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(InviteAcitivity.this, "接受失败：" + e.getMessage());
                        }
                    }
                });
            }

            //拒绝添加联系人
            @Override
            public void onReject(final InvitationInfo info) {
                //点击拒绝
                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //通知环信服务器
                            EMClient.getInstance().contactManager()
                                    .declineInvitation(info.getUserInfo().getHxid());

                            //本地
                            Modle.getInstance().getDbManager().getInvitationDao()
                                    .removeInvitation(info.getUserInfo().getHxid());
                            Modle.getInstance().getDbManager().getContactDao()
                                    .deleteContactByHxId(info.getUserInfo().getHxid());

                            //从内存删除并刷新页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "拒绝你了，嘿嘿嘿");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            refresh();
                            ShowToast.showUI(InviteAcitivity.this, "拒绝失败：" + e.getMessage());
                        }

                    }
                });

            }

            //接受群邀请
            @Override
            public void onInviteAccept(final InvitationInfo info) {
                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //网络
                            EMClient.getInstance().groupManager()
                                    .acceptInvitation(
                                            info.getGroupInfo().getGroupId(),
                                            info.getGroupInfo().getInvitePerson());
                            //本地
                            info.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                            Modle.getInstance().getDbManager().getInvitationDao()
                                    .addInvitation(info);
                            //刷新内存和页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "接受成功");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(InviteAcitivity.this, "接受失败：" + e.getMessage());
                        }
                    }
                });
            }

            //拒绝群邀请
            @Override
            public void onInviteReject(final InvitationInfo info) {

                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //网络服务器
                            EMClient.getInstance().groupManager()
                                    .declineInvitation(
                                            info.getGroupInfo().getGroupId(),
                                            info.getGroupInfo().getInvitePerson(), "");
                            //本地数据库
                            info.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
                            Modle.getInstance().getDbManager().getInvitationDao()
                                    .addInvitation(info);
                            //刷新内存和页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "拒绝成功");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(InviteAcitivity.this, "拒绝失败：" + e.getMessage());
                        }
                    }
                });

            }

            //接受群申请
            @Override
            public void onApplicationAccept(final InvitationInfo info) {

                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //网络服务器
                            EMClient.getInstance().groupManager()
                                    .acceptApplication(
                                            info.getGroupInfo().getGroupName(),
                                            info.getGroupInfo().getInvitePerson());
                            //本地数据库
                            info.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                            Modle.getInstance().getDbManager().getInvitationDao()
                                    .addInvitation(info);
                            //刷新内存页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "接受成功");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(InviteAcitivity.this, "接受失败：" + e.getMessage());
                        }
                    }
                });
            }

            //拒绝群申请
            @Override
            public void onApplicationReject(final InvitationInfo info) {
                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //网络服务器
                            EMClient.getInstance().groupManager()
                                    .declineApplication(info.getGroupInfo().getGroupName(),
                                            info.getGroupInfo().getInvitePerson(), "");
                            //本地数据库
                            info.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
                            Modle.getInstance().getDbManager().getInvitationDao()
                                    .addInvitation(info);
                            //刷新内存和页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refresh();
                                    ShowToast.show(InviteAcitivity.this, "拒绝成功");
                                }
                            });
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(InviteAcitivity.this, "拒绝失败：" + e.getMessage());
                        }
                    }
                });
            }
        });
        lvInvite.setAdapter(adapter);
        refresh();
    }

    private void refresh() {
        //获取数据库中的所有邀请信息
        List<InvitationInfo> invitations = Modle.getInstance()
                .getDbManager().getInvitationDao().getInvitations();

        //刷新信息
        if (invitations == null) {
            return;
        }
        adapter.refresh(invitations);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterReceiver(receiver);
    }
}
