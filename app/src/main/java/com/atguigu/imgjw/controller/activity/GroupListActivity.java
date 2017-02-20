package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.adapter.GroupListAdapter;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GroupListActivity extends AppCompatActivity {


    @InjectView(R.id.lv_grouplist)
    ListView lvGrouplist;
    private GroupListAdapter adapter;
    private LinearLayout llGrouphead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.inject(this);

        initView();
        initData();
        initListener();
    }

    private void initData() {
        Modle.getInstance().getGlobalThread().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络服务器获取群组信息
                    List<EMGroup> groups = EMClient.getInstance()
                            .groupManager().getJoinedGroupsFromServer();
                    //刷新页面和内存

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh();

                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void refresh() {
        List<EMGroup> allGroups = EMClient.getInstance().groupManager().getAllGroups();

        if (allGroups == null) {
            return;
        }
        adapter.refresh(allGroups);
    }

    private void initView() {
        //添加头布局
        View headView = View.inflate(this, R.layout.group_list_head, null);
        llGrouphead = (LinearLayout) headView.findViewById(R.id.ll_grouphead);
        lvGrouplist.addHeaderView(headView);
        //设置适配器
        adapter = new GroupListAdapter(this);
        lvGrouplist.setAdapter(adapter);

    }

    private void initListener() {
        llGrouphead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowToast.show(GroupListActivity.this, "组一堆");
                Intent intent = new Intent(GroupListActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        lvGrouplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                //跳转到聊天页面
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID, emGroup.getGroupId());
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        //刷新界面
        refresh();
    }
}
