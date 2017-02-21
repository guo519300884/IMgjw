package com.atguigu.imgjw.controller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.atguigu.imgjw.ImApplication;
import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.adapter.ChatDetailsAdapter;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.gv_group_detail)
    GridView gvGroupDetail;
    @InjectView(R.id.bt_group_detail)
    Button btGroupDetail;
    private String groupId;
    private EMGroup group;
    private String owner;
    private ChatDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.inject(this);

        getDate();

        initData();
        //获取群成员
        getGroupMembers();
        //群详情页面的监听
        initListener();
    }

    private void initListener() {
        gvGroupDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //获取当前gridview适配器的删除状态
                        boolean deleModle = adapter.getDeleModle();
                        //只在删除模式下才触发
                        if (deleModle) {
                            adapter.setDeleteModle(false);
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void getDate() {
        //获取群id
        groupId = getIntent().getStringExtra("groupId");
        //获取当前群组
        group = EMClient.getInstance().groupManager().getGroup(groupId);
        //获取群主
        owner = group.getOwner();
    }

    //获取群组成员
    private void getGroupMembers() {
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从服务器获取群组
                    EMGroup emGroup = EMClient.getInstance().groupManager()
                            .getGroupFromServer(groupId);
                    //获取群组成员
                    List<String> members = emGroup.getMembers();
                    //转换类型
                    final List<UserInfo> usetInfos = new ArrayList<>();

                    for (String hxId : members) {
                        usetInfos.add(new UserInfo(hxId));
                    }

                    //刷新内存 & 页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.refresh(usetInfos);
                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initData() {

        if (EMClient.getInstance().getCurrentUser().equals(owner)) {
            //群主
            btGroupDetail.setText("解散本群");
            btGroupDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Modle.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //环信服务器解散群组
                                EMClient.getInstance().groupManager()
                                        .destroyGroup(groupId);
                                //退群
                                exitGroup();
                                //结束此页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        ShowToast.show(ChatDetailsActivity.this, "此群散了屁了");
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ShowToast.showUI(ChatDetailsActivity.this, "此群没解散成：" + e.getMessage());
                            }
                        }
                    });
                }
            });
        } else {
            //成员
            btGroupDetail.setText("退出该群");
            btGroupDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Modle.getInstance().getGlobalThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //通知环信服务器退出群
                                EMClient.getInstance().groupManager()
                                        .leaveGroup(groupId);
                                exitGroup();
                                //结束该页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();

                                        ShowToast.show(ChatDetailsActivity.this, "退群成功");
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                ShowToast.show(ChatDetailsActivity.this, "退群失败：" + e.getMessage());
                            }
                        }
                    });
                }
            });
        }
        //判断是不是群主
        boolean isModify = owner.equals(EMClient.getInstance().getCurrentUser());
        adapter = new ChatDetailsAdapter(this, isModify, new MyOnMembersChangeListener());
        gvGroupDetail.setAdapter(adapter);

    }

    //群成员处理的监听
    private class MyOnMembersChangeListener implements ChatDetailsAdapter.OnMemberChangeListener {

        //删除群成员
        @Override
        public void onRemoveGroupMember(final UserInfo useriInfo) {

            new AlertDialog.Builder(ChatDetailsActivity.this)
                    .setMessage("舍得吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Modle.getInstance().getGlobalThread().execute(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        //从网络服务器删除群成员
                                        EMClient.getInstance().groupManager()
                                                .removeUserFromGroup(group.getGroupId(), useriInfo.getHxid());
                                        //重新从网络服务器获取群成员
                                        getGroupMembers();

                                        ShowToast.showUI(ChatDetailsActivity.this, "删除成功");

                                    } catch (HyphenateException e) {
                                        e.printStackTrace();
                                        ShowToast.showUI(ChatDetailsActivity.this, "删除失败：" + e.getMessage());
                                    }
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
//
//            Modle.getInstance().getGlobalThread().execute(new Runnable() {
//                @Override
//                public void run() {
//
//                    try {
//                        //从网络服务器删除群成员
//                        EMClient.getInstance().groupManager()
//                                .removeUserFromGroup(group.getGroupId(), useriInfo.getHxid());
//                        //重新从网络服务器获取群成员
//                        getGroupMembers();
//
//                        ShowToast.show(ChatDetailsActivity.this, "删除成功");
//
//                    } catch (HyphenateException e) {
//                        e.printStackTrace();
//                        ShowToast.show(ChatDetailsActivity.this, "删除失败：" + e.getMessage());
//                    }
//                }
//            });
        }

        //添加群成员
        @Override
        public void onAddGroupMember(UserInfo userInfo) {
            ShowToast.show(ChatDetailsActivity.this, "lllllllllllllll");

        }
    }

    private void exitGroup() {
        //注意上下文 用 ImApplication 相比本Activity较安全 不会发生内存泄漏
        LocalBroadcastManager manager = LocalBroadcastManager
                .getInstance(ImApplication.getContext());
        Intent intent = new Intent(Contacts.DESTORY_GROUP);
        intent.putExtra("groupId", groupId);
        manager.sendBroadcast(intent);
    }


}
