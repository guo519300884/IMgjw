package com.atguigu.imgjw.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.atguigu.imgjw.R;
import com.hyphenate.easeui.ui.EaseChatFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends AppCompatActivity {

    @InjectView(R.id.chat_fl)
    FrameLayout chatFl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.inject(this);
        initData();

    }

    private void initData() {
        EaseChatFragment chatFragment = new EaseChatFragment();
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_fl, chatFragment).commit();
    }
}
