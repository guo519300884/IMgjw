package com.atguigu.imgjw.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.controller.adapter.PickAdapter;
import com.atguigu.imgjw.modle.Modle;
import com.atguigu.imgjw.modle.bean.PickInfo;
import com.atguigu.imgjw.modle.bean.UserInfo;
import com.atguigu.imgjw.utils.ShowToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PickContactActivity extends AppCompatActivity {

    @InjectView(R.id.tv_pick_save)
    TextView tvPickSave;
    @InjectView(R.id.lv_pick)
    ListView lvPick;
    private PickAdapter adapter;
    private List<PickInfo> pickInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        ButterKnife.inject(this);

        initView();

        //获取数据
        initData();

        initListener();

    }

    private void initListener() {
        //item 的点击事件
        lvPick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取item 里面的checkbox
                CheckBox cbPick = (CheckBox) view.findViewById(R.id.cb_item_pick_contacts);
                //对当前checkbox状态取反
                cbPick.setChecked(!cbPick.isChecked());

                PickInfo pickInfo = pickInfos.get(position);
                pickInfo.setCheck(cbPick.isChecked());
                adapter.refresh(pickInfos);

            }
        });
    }

    private void initData() {
        //在本地获取联系人
        List<UserInfo> contacts = Modle.getInstance().getDbManager()
                .getContactDao().getContacts();
        if (contacts == null) {
            return;
        }
        if (contacts.size() == 0) {
            ShowToast.show(this, "你没有好友");
        }
        //转换数据
        pickInfos = new ArrayList<>();
        for (UserInfo userInfo : contacts) {
            pickInfos.add(new PickInfo(userInfo, false));
        }
        adapter.refresh(pickInfos);

    }

    private void initView() {
        adapter = new PickAdapter(this);
        lvPick.setAdapter(adapter);
    }

    //保存联系人
    @OnClick(R.id.tv_pick_save)
    public void onClick() {
        List<String> contactCheck = adapter.getContactCheck();
        if (contactCheck == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("members", contactCheck.toArray(new String[contactCheck.size()]));
        setResult(1, intent);
        //结束此页面
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回事件处理的事件
            finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
