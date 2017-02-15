package com.atguigu.imgjw.controller.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.activity.InviteAcitivity;
import com.atguigu.imgjw.utils.ShowToast;
import com.hyphenate.easeui.ui.EaseContactListFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class ContactListFragment extends EaseContactListFragment {

    @InjectView(R.id.iv_invitation_notif)
    ImageView ivInvitationNotif;
    @InjectView(R.id.ll_contact_invitation)
    LinearLayout llContactInvitation;
    @InjectView(R.id.ll_group_item)
    LinearLayout llGroupItem;
    private View view;

    @Override
    protected void initView() {
        super.initView();

        // “ + ” 标记  加好友按键
        titleBar.setRightImageResource(R.drawable.em_add);
        //" + " 的监听
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转添加好友页面
                getActivity().startActivity(new Intent(getActivity(), InviteAcitivity.class));

            }
        });

        //初始化头布局
        view = View.inflate(getActivity(), R.layout.fragment_contact_head, null);
        ButterKnife.inject(this, view);
        listView.addHeaderView(view);
    }

    @Override
    protected void setUpView() {
        super.setUpView();
    }


    @OnClick({R.id.ll_contact_invitation, R.id.ll_group_item})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_contact_invitation:
                ShowToast.show(getActivity(), "6666");
                break;
            case R.id.ll_group_item:
                ShowToast.show(getActivity(), "99999");
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
