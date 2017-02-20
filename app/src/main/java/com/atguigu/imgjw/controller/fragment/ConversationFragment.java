package com.atguigu.imgjw.controller.fragment;

import android.content.Intent;

import com.atguigu.imgjw.controller.activity.ChatActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

/**
 * Created by 皇 上 on 2017/2/14.
 */
public class ConversationFragment extends EaseConversationListFragment {

    @Override
    protected void initView() {
        super.initView();

        //会话item的点击事件
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                //跳转到聊天页面
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //携带数据
                intent.putExtra(EaseConstant.EXTRA_USER_ID, conversation.getUserName());

                if (conversation.getType()
                        == EMConversation.EMConversationType.GroupChat) {
                    //群组聊天
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);

                }

                startActivity(intent);

            }
        });

        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                //接受到新消息
                EaseUI.getInstance().getNotifier().onNewMesg(list);
                //刷新页面
                refresh();

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }


}
