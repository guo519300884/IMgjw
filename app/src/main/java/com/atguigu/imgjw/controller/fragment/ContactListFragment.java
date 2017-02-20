package com.atguigu.imgjw.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.activity.AddAcitivity;
import com.atguigu.imgjw.controller.activity.ChatActivity;
import com.atguigu.imgjw.controller.activity.GroupListActivity;
import com.atguigu.imgjw.controller.activity.InviteAcitivity;
import com.atguigu.imgjw.controller.adapter.InviteAdapter;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.ShowToast;
import com.atguigu.imgjw.utils.SpUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class ContactListFragment extends EaseContactListFragment {

    @InjectView(R.id.ll_contact_invitation)
    LinearLayout llContactInvitation;
    @InjectView(R.id.ll_group_item)
    LinearLayout llGroupItem;
    @InjectView(R.id.iv_invitation_notif)
    ImageView ivInvitationNotif;

    private View view;
    private LocalBroadcastManager manager;
    private InviteAdapter adapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //小红点
            isShow();
        }
    };
    private BroadcastReceiver contactRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refreshContacts();
        }
    };
    private List<UserInfo> contacts;
    private BroadcastReceiver groupRecrvier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isShow();
        }
    };

    @Override
    protected void initView() {
        super.initView();

        // “ + ” 标记  加好友按键
        titleBar.setRightImageResource(R.drawable.em_add);
        // 设置" + " 的监听
        titleBar.setRightLayoutClickListener(new MyAddOnClickListener());

        //初始化头布局
        view = View.inflate(getActivity(), R.layout.fragment_contact_head, null);
        ButterKnife.inject(this, view);
        listView.addHeaderView(view);
        //初始化红点
        isShow();

        //获取邀请信息变化的监听
        manager = LocalBroadcastManager.getInstance(getActivity());
        //注册
        manager.registerReceiver(receiver, new IntentFilter(Contacts.NEW_INVITE_CHAGED));
        manager.registerReceiver(contactRecevier, new IntentFilter(Contacts.CONTACT_CHAGED));
        manager.registerReceiver(groupRecrvier, new IntentFilter(Contacts.GROUP_INVITE_CHAGE));

        //初始化数据
        initData();
        //监听事件
        inInitListener();

    }

    private void inInitListener() {
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                //携带数据跳转到聊天页面
                startActivity(new Intent(getActivity(), ChatActivity.class)
                        .putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername()));

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return false;
                }

                showDialog(position);

                return true;
            }
        });

    }

    private void showDialog(final int position) {

        new AlertDialog.Builder(getActivity()).setMessage("你舍得吗？")
                .setNegativeButton("不舍得", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("舍得", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteContacts(position);
            }
        }).create().show();

    }

    private void deleteContacts(final int position) {
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取用户的环信id
                    UserInfo userInfo = contacts.get(position - 1);
                    //网络服务器中删除
                    EMClient.getInstance().contactManager()
                            .deleteContact(userInfo.getHxid());
                    //在本地删除  删除联系人
                    Modle.getInstance().getDbManager().getContactDao()
                            .deleteContactByHxId(userInfo.getHxid());
                    //删除邀请信息
                    Modle.getInstance().getDbManager().getInvitationDao()
                            .removeInvitation(userInfo.getHxid());
                    if (getActivity() == null) {
                        return;
                    }
                    //刷新页面
                    refreshContacts();
                    ShowToast.show(getActivity(), "不留了");

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    ShowToast.showUI(getActivity(), "就不走：" + e.getMessage());

                }
            }
        });

    }


    @Override
    protected void setUpView() {
        super.setUpView();
    }

    private void isShow() {
        //初始化小红点的显示  初始不显示
        boolean isRedShow = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        ivInvitationNotif.setVisibility(isRedShow ? View.VISIBLE : View.GONE);
    }

    //“ + ”的监听
    private class MyAddOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //跳转添加好友页面
            getActivity().startActivity(new Intent(getActivity(), AddAcitivity.class));
        }
    }

    @OnClick({R.id.ll_contact_invitation, R.id.ll_group_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_contact_invitation:
                //红点显示处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                isShow();
                //跳转到好友列表页面
                startActivity(new Intent(getActivity(), InviteAcitivity.class));

                break;
            case R.id.ll_group_item:
                //跳转群列表
                Intent groupIntent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(groupIntent);
                break;
        }
    }

    public void initData() {
        //获取联系人
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取所有的环信id
                    List<String> contacts = EMClient.getInstance().contactManager()
                            .getAllContactsFromServer();

                    //保存数据库 转换数据
                    List<UserInfo> userInfos = new ArrayList<UserInfo>();

                    //遍历 得到联系人
                    for (int i = 0; i < contacts.size(); i++) {
                        userInfos.add(new UserInfo(contacts.get(i)));
                    }

                    Modle.getInstance().getDbManager()
                            .getContactDao().saveContacts(userInfos, true);

                    if (getActivity() == null) {
                        return;
                    }


                    //刷新联系人列表
                    refreshContacts();


                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //刷新联系人表
    private void refreshContacts() {
        //从本地集合获取联系人信息
        contacts = Modle.getInstance()
                .getDbManager().getContactDao().getContacts();

        //校验
        if (contacts == null) {
            return;
        }
        //转换数据
        Map<String, EaseUser> maps = new HashMap<>();
        for (UserInfo userInfo : contacts) {
            EaseUser easeUser = new EaseUser(userInfo.getHxid());
            maps.put(userInfo.getHxid(), easeUser);
        }
        setContactsMap(maps);
        refresh();

    }

    @Override
    public void onResume() {
        super.onResume();

        refreshContacts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        //解除注册
        manager.unregisterReceiver(receiver);
        manager.unregisterReceiver(groupRecrvier);
        manager.unregisterReceiver(contactRecevier);
    }
}
