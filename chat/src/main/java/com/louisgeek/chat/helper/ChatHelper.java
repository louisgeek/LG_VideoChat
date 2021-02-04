package com.louisgeek.chat.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOEvent;

/**
 * Created by louisgeek on 2019/10/22.
 */
public class ChatHelper {
    private static final String TAG = "ChatHelper";
    private static final Gson mGson = new Gson();

    public static void doInvite(Context context, ChatModel chatModel, DoInviteBack doInviteBack) {
        //对讲生命周期开始
        ChatUtil.userModel = chatModel.fromUserModel;
        ChatUtil.otherUserModel = chatModel.toUserModel;
        //
        ChatUtil.online(ChatUtil.otherUserModel, new ChatUtil.OnlineBack() {
            @Override
            public void checkOnline(boolean online) {
                if (!online) {
                    Toast.makeText(context, ChatUtil.otherUserModel.userName + "不在线", Toast.LENGTH_SHORT).show();
                   /* if (onInviteBack != null) {
                        onInviteBack.onOffline(otherUserModel);
                    }*/
                    return;
                }

                //邀请
                ChatUtil.sendInvite(chatModel);
                //type 1
//                        showChatDialog(chatInfoModelJson);
                //type 2
//                ChatActivity.actionStart(mContext, chatInfoModelJson);
                if (doInviteBack != null) {
                    doInviteBack.doInvite(chatModel);
                }
            }

            @Override
            public void selfOffline() {
//                Toast.makeText(context, "设备当前不在线", Toast.LENGTH_SHORT).show();
                if (doInviteBack != null) {
                    doInviteBack.selfOffline();
                }
            }

        });

    }

    public static void onInvite(SocketIOEvent socketIOEvent, OnInviteBack onInviteBack) {
        String event = socketIOEvent.event;
        String chatModelJson = socketIOEvent.json;
        if (!ChatEvents.invite.equals(event)) {
            //其他呼叫消息 里面的页面处理
            return;
        }
        ChatModel chatModel = mGson.fromJson(chatModelJson, ChatModel.class);
        //对讲生命周期开始
        ChatUtil.userModel = chatModel.toUserModel;
        ChatUtil.otherUserModel = chatModel.fromUserModel;
//                showChat(chatInfoModelJson);
        if (onInviteBack != null) {
            onInviteBack.onInvite(chatModel);
        }
    }

    public interface DoInviteBack {
        void doInvite(ChatModel chatModel);

        void selfOffline();
    }

    public interface OnInviteBack {
        void onInvite(ChatModel chatModel);

    }
}
