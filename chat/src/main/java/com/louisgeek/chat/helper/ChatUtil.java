package com.louisgeek.chat.helper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.model.InfoChatModel;
import com.louisgeek.chat.model.SdpInfoChatModel;
import com.louisgeek.chat.model.SdpTypeChatModel;
import com.louisgeek.chat.model.UserModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOManager;

import io.socket.client.Ack;
import io.socket.client.Socket;

/**
 * Created by louisgeek on 2019/9/11.
 */
public class ChatUtil {
    private static final String TAG = "ChatUtil";
    private static final Gson mGson = new Gson();
    public static UserModel userModel;
    public static UserModel otherUserModel;

    public static void online(UserModel otherUserModel, OnlineBack onlineBack) {
        String userModelJson = mGson.toJson(otherUserModel);
        Socket socket = SocketIOManager.getInstance().socket();
        if (socket != null && socket.connected()) {
            socket.emit(ChatEvents.online, userModelJson, new Ack() {
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
                                onlineBack.checkOnline("1".equals(online));
                            }
                        }
                    });

                }
            });
        } else {
            if (onlineBack != null) {
                onlineBack.selfOffline();
            }
        }

    }

    /**
     * 不推荐直接发送
     *
     * @param chatModel
     * @return
     */
    @Deprecated
    public static String sendInvite(ChatModel chatModel) {
        //
        chatModel.fromUserModel = ChatUtil.userModel;
        chatModel.toUserModel = ChatUtil.otherUserModel;
        String json = mGson.toJson(chatModel);
        emit(ChatEvents.invite, json);
        //
        return json;
    }

    public static void sendCancel(boolean isTimeout) {
        InfoChatModel infoChatModel = new InfoChatModel();
        infoChatModel.fromUserModel = userModel;
        infoChatModel.toUserModel = otherUserModel;
        infoChatModel.info = String.valueOf(isTimeout);
        String json = mGson.toJson(infoChatModel);
        emit(ChatEvents.cancel, json);
    }


    public static void sendAgree() {
        InfoChatModel infoChatModel = new InfoChatModel();
        infoChatModel.fromUserModel = userModel;
        infoChatModel.toUserModel = otherUserModel;
        infoChatModel.info = "同意信息";
        String json = mGson.toJson(infoChatModel);
        emit(ChatEvents.agree, json);
    }

    public static void sendReject() {
        InfoChatModel infoChatModel = new InfoChatModel();
        infoChatModel.fromUserModel = userModel;
        infoChatModel.toUserModel = otherUserModel;
        infoChatModel.info = "拒接信息";
        String json = mGson.toJson(infoChatModel);
        emit(ChatEvents.reject, json);
    }

    public static void sendOffer(SdpTypeChatModel sdpTypeChatModel) {
        sdpTypeChatModel.fromUserModel = userModel;
        sdpTypeChatModel.toUserModel = otherUserModel;
        String json = mGson.toJson(sdpTypeChatModel);
        emit(ChatEvents.offer, json);
    }

    public static void sendAnswer(SdpTypeChatModel sdpTypeChatModel) {
        sdpTypeChatModel.fromUserModel = userModel;
        sdpTypeChatModel.toUserModel = otherUserModel;
        String json = mGson.toJson(sdpTypeChatModel);
        emit(ChatEvents.answer, json);
    }

    public static void sendCandidate(SdpInfoChatModel sdpInfoChatModel) {
        sdpInfoChatModel.fromUserModel = userModel;
        sdpInfoChatModel.toUserModel = otherUserModel;
        String json = mGson.toJson(sdpInfoChatModel);
        emit(ChatEvents.candidate, json);
    }

    public static void sendChatEnd() {
        InfoChatModel infoChatModel = new InfoChatModel();
        infoChatModel.fromUserModel = userModel;
        infoChatModel.toUserModel = otherUserModel;
        infoChatModel.info = "挂断信息";
        String json = mGson.toJson(infoChatModel);
        emit(ChatEvents.end, json);
    }

    public static void sendSwitchVideo(boolean isVideo) {
        InfoChatModel infoChatModel = new InfoChatModel();
        infoChatModel.fromUserModel = userModel;
        infoChatModel.toUserModel = otherUserModel;
        infoChatModel.info = String.valueOf(isVideo);
        String json = mGson.toJson(infoChatModel);
        emit(ChatEvents.switchVideo2Audio, json);
    }

  /*  public static void appUpdate() {
        if (useSocket) {
//            SocketManager.getInstance().socket().emit(SocketEvents.appUpdate);
            return;
        }
        String topic = String.format("im/%s/appUpdate");
//        MessageManager.getInstance().publish(topic, "");
    }*/

    private static void emit(String event, String json) {
        Socket socket = SocketIOManager.getInstance().socket();
        if (socket == null) {
            Log.e(TAG, "socket is null");
            return;
        }
        if (socket.connected()) {
            Log.e(TAG, "socket is not connected");
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
        void checkOnline(boolean online);

        void selfOffline();
    }

}
