package com.louisgeek.chat.helper;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.louisgeek.chat.helper.base.BaseUserChatHelper;
import com.louisgeek.chat.model.ChatInfoTypeModel;
import com.louisgeek.chat.model.base.ChatInfoModel;
import com.louisgeek.chat.model.base.UserModel;
import com.louisgeek.chat.model.info.VideoChatConfigModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOEvent;

/**
 * Created by louisgeek on 2019/10/22.
 */
public class UserChatHelper {
    private static final String TAG = "CallHelper";
    private static final Gson mGson = new Gson();

    public static void doInvite(Context context, UserModel userModel, UserModel otherUserModel, OnInviteBack onInviteBack) {
        //
        BaseUserChatHelper.online(otherUserModel, new BaseUserChatHelper.OnlineBack() {
            @Override
            public void online(boolean isOnline) {
                if (!isOnline) {
                    Toast.makeText(context, otherUserModel.userName + "不在线", Toast.LENGTH_SHORT).show();
                   /* if (onInviteBack != null) {
                        onInviteBack.onOffline(otherUserModel);
                    }*/
                    return;
                }
                //
                ChatLogicHelper.userModel = userModel;
                ChatLogicHelper.otherUserModel = otherUserModel;
                //只需要关注 VideoChatConfigModel
                VideoChatConfigModel videoChatConfigModel = new VideoChatConfigModel();
                videoChatConfigModel.isVideoInitConfig = true;
                //
//                videoChatConfigModel.videoChatParameter = true;
                //邀请
                String chatInfoModelJson = BaseUserChatHelper.sendInvite(userModel, otherUserModel, videoChatConfigModel);
                //type 1
//                        showChatDialog(chatInfoModelJson);
                //type 2
//                ChatActivity.actionStart(mContext, chatInfoModelJson);
                if (onInviteBack != null) {
                    onInviteBack.onInvite(chatInfoModelJson);
                }
            }
        });

    }

    public static void onInvite(SocketIOEvent socketIOEvent, OnInviteBack onInviteBack) {
        String event = socketIOEvent.event;
        String chatInfoModelJson = socketIOEvent.json;
        if (!ChatEvents.userChat.equals(event)) {
            //其他消息 不关心
            return;
        }
        ChatInfoModel chatInfoModelTemp = mGson.fromJson(chatInfoModelJson, ChatInfoModel.class);
        String chatInfoType = chatInfoModelTemp.chatInfoType;
        if (!ChatInfoTypeModel.ChatInfo_Invite.equals(chatInfoType)) {
            //其他呼叫消息 里面的页面处理
            return;
        }
        //
        ChatLogicHelper.userModel = chatInfoModelTemp.toUserModel;
        ChatLogicHelper.otherUserModel = chatInfoModelTemp.fromUserModel;
//                showChat(chatInfoModelJson);
        if (onInviteBack != null) {
            onInviteBack.onInvite(chatInfoModelJson);
        }
    }

    public interface OnInviteBack {
        void onInvite(String chatInfoModelJson);
    }
}
