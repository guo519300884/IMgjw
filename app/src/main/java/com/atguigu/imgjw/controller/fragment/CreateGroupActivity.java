package com.atguigu.imgjw.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.utils.ShowToast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity {

    @InjectView(R.id.et_newgroup_name)
    EditText etNewgroupName;
    @InjectView(R.id.et_newgroup_desc)
    EditText etNewgroupDesc;
    @InjectView(R.id.cb_newgroup_public)
    CheckBox cbNewgroupPublic;
    @InjectView(R.id.cb_newgroup_invite)
    CheckBox cbNewgroupInvite;
    @InjectView(R.id.bt_newgroup_create)
    Button btNewgroupCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.bt_newgroup_create)
    public void onClick() {
        if (validate()) {
            //跳转
            Intent intent = new Intent(CreateGroupActivity.this, PickContactActivity.class);
            startActivity(intent);
        }
    }

    private boolean validate() {

        String groupName = etNewgroupName.getText().toString().trim();
        String desc = etNewgroupDesc.getText().toString().trim();
        if (TextUtils.isEmpty(groupName)) {
            ShowToast.show(this, "群名称不能为空");
            return false;
        }
        if (TextUtils.isEmpty(desc)) {
            ShowToast.show(this, "群介绍不能为空");
            return false;
        }
        return true;
    }
}
