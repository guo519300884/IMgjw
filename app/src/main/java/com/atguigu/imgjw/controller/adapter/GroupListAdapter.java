package com.atguigu.imgjw.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.atguigu.imgjw.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 皇 上 on 2017/2/18.
 */
public class GroupListAdapter extends BaseAdapter {

    private final Context context;
    private List<EMGroup> groups;

    public GroupListAdapter(Context context) {
        this.context = context;
        groups = new ArrayList<>();
    }

    public void refresh(List<EMGroup> groups) {
        if (groups == null) {
            return;
        }
        //清除
        this.groups.clear();
        //添加
        this.groups.addAll(groups);
        //刷新

        notifyDataSetChanged();


    }


    @Override
    public int getCount() {
        return groups == null ? 0 : groups.size();
    }

    @Override
    public Object getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_group, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EMGroup emGroup = groups.get(position);
        viewHolder.groupTvGroupname.setText(emGroup.getGroupId());

        return convertView;
    }


    class ViewHolder {
        @InjectView(R.id.group_tv_groupname)
        TextView groupTvGroupname;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
