package com.louisgeek.chat.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.model.UserModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOEvent;

/**
 * Created by louisgeek on 2019/10/22.
 */
public class ChatHelper {
    private static final String TAG = "ChatHelper";
    private static final Gson mGson = new Gson();

    public static void doInvite(Context context, UserModel userModel, UserModel otherUserModel, ChatModel chatModel, OnInviteBack onInviteBack) {
        //
        ChatUtil.online(otherUserModel, new ChatUtil.OnlineBack() {
            @Override
            public void online(boolean isOnline) {
                if (!isOnline) {
                    Toast.makeText(context, otherUserModel.userName + "不在线", Toast.LENGTH_SHORT).show();
                   /* if (onInviteBack != null) {
                        onInviteBack.onOffline(otherUserModel);
                    }*/
                    return;
                }
                //对讲生命周期开始
                ChatUtil.userModel = userModel;
                ChatUtil.otherUserModel = otherUserModel;
                //邀请
                ChatUtil.sendInvite(chatModel);
                //type 1
//                        showChatDialog(chatInfoModelJson);
                //type 2
//                ChatActivity.actionStart(mContext, chatInfoModelJson);
                if (onInviteBack != null) {
                    onInviteBack.onInvite(chatModel);
                }
            }
        });

    }

    public static void onInvite(SocketIOEvent socketIOEvent, OnInviteBack onInviteBack) {
        String event = socketIOEvent.event;
        String chatInfoModelJson = socketIOEvent.json;
        if (!ChatEvents.invite.equals(event)) {
            //其他呼叫消息 里面的页面处理
            return;
        }
        ChatModel chatModel = mGson.fromJson(chatInfoModelJson, ChatModel.class);
        //对讲生命周期开始
        ChatUtil.userModel = chatModel.toUserModel;
        ChatUtil.otherUserModel = chatModel.fromUserModel;
//                showChat(chatInfoModelJson);
        if (onInviteBack != null) {
            onInviteBack.onInvite(chatModel);
        }
    }

    public interface OnInviteBack {
        void onInvite(ChatModel chatModel);
    }
}