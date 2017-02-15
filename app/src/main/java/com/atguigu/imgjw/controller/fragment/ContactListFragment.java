package com.atguigu.imgjw.controller.fragment;

import android.content.Intent;
import android.view.View;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.activity.AddContactActivity;
import com.hyphenate.easeui.ui.EaseContactListFragment;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class ContactListFragment extends EaseContactListFragment {

    private View view;

    @Override
    protected void initView() {
        super.initView();

        titleBar.setRightImageResource(R.drawable.em_add);

        //添加头布局
        view = View.inflate(getActivity(), R.layout.fragment_contact_head, null);
        listView.addHeaderView(view);
    }

    @Override
    protected void setUpView() {
        super.setUpView();

        inInitListener();
    }

    private void inInitListener() {
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //跳转加好友页面
                getActivity().startActivity(new Intent(getActivity(),AddContactActivity.class));

            }
        });
    }
}
