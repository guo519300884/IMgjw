package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.atguigu.imgjw.ImApplication;
import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.utils.Contacts;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.gv_group_detail)
    GridView gvGroupDetail;
    @InjectView(R.id.bt_group_detail)
    Button btGroupDetail;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        //获取群id
        final String groupId = getIntent().getStringExtra("groupId");

        if (TextUtils.isEmpty(groupId)) {
            return;
        }
        //获取当前群组
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
        //获取群主
        String owner = group.getOwner();

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

    }

    private void exitGroup() {
        //注意上下文 用 ImApplication 较安全
        LocalBroadcastManager manager = LocalBroadcastManager
                .getInstance(ImApplication.getContext());
        Intent intent = new Intent(Contacts.DESTORY_GROUP);
        intent.putExtra("groupId", groupId);
        manager.sendBroadcast(intent);
    }
}
