package com.atguigu.imgjw.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.activity.AddAcitivity;
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
    private LocalBroadcastManager lbm;
    private InviteAdapter adapter;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //小红点
            isShow();
            //联系人邀请信息变化
            adapter.refresh(Modle.getInstance()
                    .getDbManager().getInvitationDao()
                    .getInvitations());
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
        lbm = LocalBroadcastManager.getInstance(getActivity());
        //注册
        lbm.registerReceiver(receiver, new IntentFilter(Contacts.NEW_INVITE_CHAGED));

        initData();
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
                ShowToast.show(getActivity(), "6666");
                //红点显示处理
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                isShow();
                //跳转到好友列表页面
                startActivity(new Intent(getActivity(), InviteAcitivity.class));

                break;
            case R.id.ll_group_item:
                ShowToast.show(getActivity(), "99999");


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
        List<UserInfo> contacts = Modle.getInstance()
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
        lbm.unregisterReceiver(receiver);
    }


}
