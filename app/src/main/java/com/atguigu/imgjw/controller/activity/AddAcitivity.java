package com.atguigu.imgjw.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AddAcitivity extends AppCompatActivity {


    @InjectView(R.id.invite_btn_search)
    Button inviteBtnSearch;
    @InjectView(R.id.invite_et_search)
    EditText inviteEtSearch;
    @InjectView(R.id.invite_tv_username)
    TextView inviteTvUsername;
    @InjectView(R.id.invite_btn_add)
    Button inviteBtnAdd;
    @InjectView(R.id.invite_ll_item)
    LinearLayout inviteLlItem;

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ButterKnife.inject(this);

    }


    @OnClick({R.id.invite_btn_search, R.id.invite_btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invite_btn_search:
                //搜索

                //验证
                if (validate()) {
                    //显示查找的结果
                    inviteLlItem.setVisibility(View.VISIBLE);
                    //设置查找到的好友名
                    inviteTvUsername.setText(username);
                } else {
                    inviteLlItem.setVisibility(View.GONE);
                }
                break;
            case R.id.invite_btn_add:
                //添加好友在分线程中进行
                Modle.getInstance().getGlobalThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        //进入环信服务器添加好友
                        try {
                            //此参数为查找到的好友username 和添加理由
                            EMClient.getInstance().contactManager()
                                    .addContact(username, "添加好友");
                            ShowToast.showUI(AddAcitivity.this, "添加成功");
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                            ShowToast.showUI(AddAcitivity.this, "添加失败" + e.getMessage());
                        }

                    }
                });
                break;
        }
    }

    private boolean validate() {
        //本地验证
        username = inviteEtSearch.getText().toString().trim();
        //判断
        if (TextUtils.isEmpty(username)) {
            ShowToast.show(this, "用户名不能为空");
            return false;
        }
        //服务器验证
        return true;
    }

}
