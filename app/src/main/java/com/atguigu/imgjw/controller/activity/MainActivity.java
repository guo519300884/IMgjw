package com.atguigu.imgjw.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.fragment.ContactListFragment;
import com.atguigu.imgjw.controller.fragment.ConversationFragment;
import com.atguigu.imgjw.controller.fragment.SettingFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.main_fl)
    FrameLayout mainFl;
    @InjectView(R.id.rg_main)
    RadioGroup rgMain;

    private Fragment conversationFragment;
    private Fragment contactListFragment;
    private Fragment settingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();
        initListener();
        //默认聊天页面
        switchFragmet(R.id.rb_main_chat);
    }

    private void initListener() {
        rgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //选择页面
                switchFragmet(checkedId);
            }
        });
    }

    //选择页面
    private void switchFragmet(int checkedId) {
        Fragment fragment = null;
        switch (checkedId) {
            case R.id.rb_main_chat:
                fragment = conversationFragment;
                break;
            case R.id.rb_main_contact:
                fragment = contactListFragment;
                break;
            case R.id.rb_main_setting:
                fragment = settingFragment;
                break;
            default:
                fragment = conversationFragment;
                break;
        }
        if (fragment == null) {
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fl, fragment).commit();
    }


    private void initData() {
        // 创建三个fragment
        conversationFragment = new ConversationFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();
    }
}
