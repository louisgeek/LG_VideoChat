package com.louisgeek.chat.helper.base;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.louisgeek.chat.helper.ChatLogicHelper;
import com.louisgeek.chat.model.ChatInfoTypeModel;
import com.louisgeek.chat.model.base.ChatInfoModel;
import com.louisgeek.chat.model.base.UserModel;
import com.louisgeek.chat.model.info.VideoChatConfigModel;
import com.louisgeek.chat.model.info.VideoChatInfoModel;
import com.louisgeek.chat.model.info.VideoChatSdpInfoModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOManager;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by louisgeek on 2019/9/11.
 */
public class BaseUserChatHelper {
    private static final String TAG = "MessageSocketAdapter";
    //useSockect
    public static final boolean useSocket = true;

    public static void online(UserModel otherUserModel, OnlineBack onlineBack) {
        String userModelJson = new Gson().toJson(otherUserModel);
        emit(ChatEvents.online, userModelJson, new Ack() {
            @Override
            public void call(Object... args) {
                String userId = (String) args[0];
                String online = (String) args[1];
                Log.e(TAG, "call: userId " + userId + " online " + online);
                //
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (onlineBack != null) {
                            boolean isOnline = online.equals("1");
                            onlineBack.online(isOnline);
                        }
                    }
                });

            }
        });
    }

    public static String sendInvite(UserModel fromUserModel, UserModel otherUserModel, VideoChatConfigModel videoChatConfigModel) {
        ChatInfoModel<VideoChatConfigModel> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = fromUserModel;
        chatInfoModel.toUserModel = otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Invite;
        chatInfoModel.chatInfo = videoChatConfigModel;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
        //
        return chatInfoModelJson;
    }

    public static void sendCancel(boolean isTimeout) {
        ChatInfoModel<Boolean> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Cancel;
        chatInfoModel.chatInfo = isTimeout;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }


    public static void sendAgree() {
        ChatInfoModel<String> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Agree;
        chatInfoModel.chatInfo = "chatInfo";
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendReject() {
        ChatInfoModel<String> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Reject;
        chatInfoModel.chatInfo = "chatInfo";
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendOffer(VideoChatSdpInfoModel videoChatSdpInfoModel) {
        ChatInfoModel<VideoChatSdpInfoModel> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Offer;
        chatInfoModel.chatInfo = videoChatSdpInfoModel;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendAnswer(VideoChatSdpInfoModel videoChatSdpInfoModel) {
        ChatInfoModel<VideoChatSdpInfoModel> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Answer;
        chatInfoModel.chatInfo = videoChatSdpInfoModel;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendCandidate(VideoChatInfoModel videoChatInfoModel) {
        ChatInfoModel<VideoChatInfoModel> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_Candidate;
        chatInfoModel.chatInfo = videoChatInfoModel;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendChatEnd() {
        ChatInfoModel<String> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_ChatEnd;
        chatInfoModel.chatInfo = "chatInfo";
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void sendSwitchVideo(boolean isVideo) {
        ChatInfoModel<Boolean> chatInfoModel = new ChatInfoModel<>();
        chatInfoModel.fromUserModel = ChatLogicHelper.userModel;
        chatInfoModel.toUserModel = ChatLogicHelper.otherUserModel;
        chatInfoModel.chatInfoType = ChatInfoTypeModel.ChatInfo_SwitchVideo2Audio;
        chatInfoModel.chatInfo = isVideo;
        String chatInfoModelJson = new Gson().toJson(chatInfoModel);
        emit(ChatEvents.userChat, chatInfoModelJson);
    }

    public static void appUpdate() {
        if (useSocket) {
//            SocketManager.getInstance().socket().emit(SocketEvents.appUpdate);
            return;
        }
        String topic = String.format("im/%s/appUpdate");
//        MessageManager.getInstance().publish(topic, "");
    }

    private static void emit(String event, String json) {
        Socket socket = SocketIOManager.getInstance().socket();
        if (socket == null) {
            Log.e(TAG, "socket is null");
            return;
        }
        socket.emit(event, json);
    }

    private static void emit(String event, String json, Ack ack) {
        Socket socket = SocketIOManager.getInstance().socket();
        if (socket == null) {
            Log.e(TAG, "socket is null");
            return;
        }
        socket.emit(event, json, ack);
    }

    public interface OnlineBack {
        void online(boolean isOnline);
    }

}
