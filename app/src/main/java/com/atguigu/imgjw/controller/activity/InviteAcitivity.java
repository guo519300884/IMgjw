package com.atguigu.imgjw.controller.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atguigu.imgjw.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class InviteAcitivity extends AppCompatActivity {

    @InjectView(R.id.invite_btn_save)
    Button inviteBtnSave;
    @InjectView(R.id.invite_et_search)
    EditText inviteEtSearch;
    @InjectView(R.id.invite_tv_username)
    TextView inviteTvUsername;
    @InjectView(R.id.invite_btn_add)
    Button inviteBtnAdd;
    @InjectView(R.id.invite_ll_item)
    LinearLayout inviteLlItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.inject(this);
    }

    @OnClick({R.id.invite_btn_save, R.id.invite_btn_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invite_btn_save:

                break;
            case R.id.invite_btn_add:

                break;
        }
    }
}
