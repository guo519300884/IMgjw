package com.atguigu.imgjw.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by 皇 上 on 2017/2/21.
 */
public class ChatDetailsAdapter extends BaseAdapter {

    private final Context context;
    private final boolean isModify;
    private List<UserInfo> userInfos;
    private boolean isDeleteModle = false;

    public ChatDetailsAdapter(Context context, boolean isModify, OnMemberChangeListener onMembersChangeListener) {
        this.context = context;
        this.isModify = isModify;
        this.onMembersChangeListener = onMembersChangeListener;
        userInfos = new ArrayList<>();
    }

    public void refresh(List<UserInfo> userInfos) {

        if (userInfos == null || userInfos.size() == 0) {
            return;
        }

        //清除原有数据
        this.userInfos.clear();
        //添加  “ + ” “ - ”
        initUser();
        //添加群成员  0 :确保群成员在前面
        this.userInfos.addAll(0, userInfos);
        //刷新
        notifyDataSetChanged();

    }

    private void initUser() {
        this.userInfos.add(new UserInfo("add"));
        this.userInfos.add(new UserInfo("remove"));

    }

    @Override
    public int getCount() {
        return userInfos == null ? 0 : userInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return userInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapter_group_memebers, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //群主 处理视图
        if (isModify) {
            if (position == userInfos.size() - 1) {//减号
                if (isDeleteModle) {
                    //删除模式下
                    convertView.setVisibility(View.GONE);//隐藏减号

                } else {
                    convertView.setVisibility(View.VISIBLE);//显示减号
                    viewHolder.ivMemberDelete.setVisibility(View.GONE);//隐藏删除用小减号
                    viewHolder.tvMemberName.setVisibility(View.INVISIBLE);//隐藏名字 占位
                    viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_smiley_minus_btn_pressed);//设置图片
                }
                //加号
            } else if (position == userInfos.size() - 2) {

                if (isDeleteModle) {
                    //删除模式下
                    convertView.setVisibility(View.GONE);//隐藏加号
                } else {
                    convertView.setVisibility(View.VISIBLE);//显示加号
                    viewHolder.ivMemberDelete.setVisibility(View.GONE);//隐藏删除用小减号
                    viewHolder.tvMemberName.setVisibility(View.INVISIBLE);//隐藏名字 占位
                    viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_smiley_add_btn_pressed);//设置图片
                }

            } else {
                //群成员上刪除用小减号
                convertView.setVisibility(View.VISIBLE);
                viewHolder.tvMemberName.setVisibility(View.VISIBLE);
                //根据是否删除模式选择是否显示删除用小减号
                if (isDeleteModle) {
                    viewHolder.ivMemberDelete.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ivMemberDelete.setVisibility(View.GONE);
                }
                //设置设置群成员的用户名
                viewHolder.tvMemberName.setText(userInfos.get(position).getUsername());
                viewHolder.ivMemberPhoto.setImageResource(R.mipmap.em_default_avatar);
            }

            //加号减号的监听事件
            if (position == userInfos.size() - 1) {//减号
                viewHolder.ivMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //判断是否为删除模式
                        if (!isDeleteModle) {
                            //不是就设置为是
                            isDeleteModle = true;
                            //刷新进入删除模式
                            notifyDataSetChanged();
                        }
                    }
                });
            } else if (position == userInfos.size() - 2) {//加号
                viewHolder.ivMemberPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (onMembersChangeListener != null) {
                            //添加群群成员
                            onMembersChangeListener.onAddGroupMember(userInfos.get(position));
                        }
                    }
                });
            } else {
                //删除用小减号
                viewHolder.ivMemberDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMembersChangeListener != null) {
                            //删除群成员
                            onMembersChangeListener.onRemoveGroupMember(userInfos.get(position));
                        }
                    }
                });
            }
        } else {
            //群成员
            if (position == userInfos.size() - 1) {
                //不显示减号
                convertView.setVisibility(View.GONE);
            } else if (position == userInfos.size() - 2) {
                //不显示加号
                convertView.setVisibility(View.GONE);
            } else {
                //显示群内所有成员
                convertView.setVisibility(View.VISIBLE);
                //设置名字
                viewHolder.tvMemberName.setText(userInfos.get(position).getUsername());
                //不显示删除用小减号
                viewHolder.ivMemberDelete.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.iv_member_photo)
        ImageView ivMemberPhoto;
        @InjectView(R.id.tv_member_name)
        TextView tvMemberName;
        @InjectView(R.id.iv_member_delete)
        ImageView ivMemberDelete;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    private OnMemberChangeListener onMembersChangeListener;

    public interface OnMemberChangeListener {

        void onRemoveGroupMember(UserInfo useriInfo);

        void onAddGroupMember(UserInfo userInfo);
    }

}
