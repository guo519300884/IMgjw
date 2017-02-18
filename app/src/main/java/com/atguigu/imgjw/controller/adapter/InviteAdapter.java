package com.atguigu.imgjw.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.atguigu.imgjw.R;
import com.atguigu.imgjw.modle.bean.GroupInfo;
import com.atguigu.imgjw.modle.bean.InvitationInfo;
import com.atguigu.imgjw.modle.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by 皇 上 on 2017/2/16.
 */

public class InviteAdapter extends BaseAdapter {

    private final Context context;
    private List<InvitationInfo> invitationInfos;

    public InviteAdapter(Context context, OnInviteChangeListener onInviteChangeListener) {
        this.context = context;
        invitationInfos = new ArrayList<>();
        this.onInviteChangeListener = onInviteChangeListener;
    }

    public void refresh(List<InvitationInfo> invitationInfos) {

        //校验
        if (invitationInfos == null) {
            return;
        }
        //清空原有数据
        this.invitationInfos.clear();
        //添加数据
        this.invitationInfos.addAll(invitationInfos);
        //刷新数据
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return invitationInfos == null ? 0 : invitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return invitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建ViewHolder
        ViewHolder viewHolder = null;
        if (convertView == null) {
            //创建convertView
            convertView = View.inflate(context, R.layout.item_invite, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //数据绑定
        final InvitationInfo invitationInfo = invitationInfos.get(position);
        GroupInfo groupInfo = invitationInfo.getGroupInfo();

        if (groupInfo != null) {
            //群邀请
            if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION) {
//                //显示俩按钮
//                viewHolder.btInviteAccept.setVisibility(View.VISIBLE);
//                viewHolder.btInviteReject.setVisibility(View.VISIBLE);
//                //设置原因
//                if (invitationInfo.getReason() == null) {
//                    viewHolder.tvInviteReason.setText("邀请好友");
//                } else {
//                    viewHolder.tvInviteReason.setText(invitationInfo.getReason());
//                }

                //接受按钮的监听
                viewHolder.btInviteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onAccept(invitationInfo);
                    }
                });
                //拒绝按钮的监听
                viewHolder.btInviteReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onReject(invitationInfo);
                    }
                });
            } else if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE) {

                //接受按钮的监听
                viewHolder.btInviteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onAccept(invitationInfo);
                    }
                });
                //拒绝按钮的监听
                viewHolder.btInviteReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onReject(invitationInfo);
                    }
                });
                UserInfo userInfo = invitationInfo.getUserInfo();
                viewHolder.tvInviteName.setText(userInfo.getUsername());

                //隐藏 俩按钮
                viewHolder.btInviteAccept.setVisibility(View.GONE);
                viewHolder.btInviteReject.setVisibility(View.GONE);

                //显示原因
                switch (invitationInfo.getStatus()) {

                    case GROUP_APPLICATION_ACCEPTED:
                        viewHolder.tvInviteReason.setText("您的群申请已经被对方接受");
                        break;
                    case GROUP_INVITE_ACCEPTED:
                        viewHolder.tvInviteReason.setText("您的群邀请已经被对方接受");
                        break;
                    case GROUP_APPLICATION_DECLINED:
                        viewHolder.tvInviteReason.setText("你的群申请被对方拒绝");
                        break;
                    case GROUP_INVITE_DECLINED:
                        viewHolder.tvInviteReason.setText("您的群邀请被对方拒绝");
                        break;
                    case NEW_GROUP_INVITE:
                        viewHolder.btInviteAccept.setVisibility(View.VISIBLE);
                        viewHolder.btInviteReject.setVisibility(View.VISIBLE);
                        viewHolder.tvInviteReason.setText("您收到了群邀请");
                        break;
                    case NEW_GROUP_APPLICATION:
                        viewHolder.btInviteAccept.setVisibility(View.VISIBLE);
                        viewHolder.btInviteReject.setVisibility(View.VISIBLE);
                        viewHolder.tvInviteReason.setText("您收到了群申请");
                        break;
                    case GROUP_ACCEPT_INVITE:
                        viewHolder.tvInviteReason.setText("你接受了群邀请");
                        break;
                    case GROUP_ACCEPT_APPLICATION:
                        viewHolder.tvInviteReason.setText("您接受了群申请");
                        break;
                }

            }
        } else {
            //联系人邀请
            UserInfo userInfo = invitationInfo.getUserInfo();
            viewHolder.tvInviteName.setText(userInfo.getUsername());
            //隐藏 俩按钮
            viewHolder.btInviteAccept.setVisibility(View.GONE);
            viewHolder.btInviteReject.setVisibility(View.GONE);
            //新邀请
            if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.NEW_INVITE) {
                //显示俩按钮
                viewHolder.btInviteAccept.setVisibility(View.VISIBLE);
                viewHolder.btInviteReject.setVisibility(View.VISIBLE);
                //设置原因
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvInviteReason.setText("邀请好友");
                } else {
                    viewHolder.tvInviteReason.setText(invitationInfo.getReason());
                }

                //接受按钮的监听
                viewHolder.btInviteAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onAccept(invitationInfo);
                    }
                });
                //拒绝按钮的监听
                viewHolder.btInviteReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInviteChangeListener.onReject(invitationInfo);
                    }
                });
            } else if (invitationInfo.getStatus()//邀请被接受
                    == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvInviteReason.setText("邀请被接受");
                } else {
                    viewHolder.tvInviteReason.setText(invitationInfo.getReason());
                }
            } else if (invitationInfo.getStatus()
                    == InvitationInfo.InvitationStatus.INVITE_ACCEPT) {
                if (invitationInfo.getReason() == null) {
                    viewHolder.tvInviteReason.setText("接受邀请");
                } else {
                    viewHolder.tvInviteReason.setText(invitationInfo.getReason());
                }
            }
        }
        //返回布局
        return convertView;
    }

    class ViewHolder {
        @InjectView(R.id.tv_invite_name)
        TextView tvInviteName;
        @InjectView(R.id.tv_invite_reason)
        TextView tvInviteReason;
        @InjectView(R.id.bt_invite_accept)
        Button btInviteAccept;
        @InjectView(R.id.bt_invite_reject)
        Button btInviteReject;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    /**
     * 1.定义接口
     * 2.定义接口的变量
     * 3.设置set方法
     * 4.接受接口的实例化对象
     * 5.调用接口的方法
     */
    public interface OnInviteChangeListener {
        //接受按钮
        void onAccept(InvitationInfo info);

        //拒绝按钮
        void onReject(InvitationInfo info);
        //

    }

    private OnInviteChangeListener onInviteChangeListener;

    public void setOnInviteChangeListener(OnInviteChangeListener onInviteChangeListener) {
        this.onInviteChangeListener = onInviteChangeListener;
    }
}
