package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
        boolean isModify  = owner.equals(EMClient.getInstance().getCurrentUser());

        //
        adapter = new ChatDetailsAdapter(this,isModify);
        gvGroupDetail.setAdapter(adapter);

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
