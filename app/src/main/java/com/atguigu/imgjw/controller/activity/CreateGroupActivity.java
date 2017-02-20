package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

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

    private String groupName;
    private String desc;

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

            startActivityForResult(intent, 1);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //创建群组
            crerteGroup(data);
        }
    }

    private void crerteGroup(final Intent data) {
        /**


         * 创建群组


         * @param groupName 群组名称


         * @param desc 群组简介


         * @param allMembers 群组初始成员，如果只有自己传空数组即可


         * @param reason 邀请成员加入的reason


         * @param option 群组类型选项，可以设置群组最大用户数(默认200)及群组类型@see {@link EMGroupStyle}


         * @return 创建好的group


         * @throws HyphenateException


         */
        if (data == null) {
            return;
        }
        Modle.getInstance().getGlobalThread().execute(new Runnable() {

            @Override
            public void run() {

                try {

                    String[] memberses = data.getStringArrayExtra("members");
                    //在环信服务器中创建群
                    EMGroupManager.EMGroupOptions options = new EMGroupManager.EMGroupOptions();

                    /**
                     * EMGroupStylePrivateOnlyOwnerInvite——私有群，只有群主可以邀请人；
                     * EMGroupStylePrivateMemberCanInvite——私有群，群成员也能邀请人进群；
                     * EMGroupStylePublicJoinNeedApproval——公开群，加入此群除了群主邀请，只能通过申请加入此群；
                     * EMGroupStylePublicOpenJoin ——公开群，任何人都能加入此群。
                     */

                    //设置群容量
                    options.maxUsers = 200;

                    if (cbNewgroupPublic.isChecked()) {
                        if (cbNewgroupInvite.isChecked()) {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                        } else {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                        }
                    } else {
                        if (cbNewgroupInvite.isChecked()) {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                        } else {
                            options.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                        }
                    }

                    EMClient.getInstance().groupManager()
                            .createGroup(groupName, desc, memberses, "", options);

                    ShowToast.showUI(CreateGroupActivity.this, "创建成功");

                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ShowToast.showUI(CreateGroupActivity.this, "创建失败：" + e.getMessage());
                }
            }
        });

    }

    private boolean validate() {

        groupName = etNewgroupName.getText().toString().trim();
        desc = etNewgroupDesc.getText().toString().trim();
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
