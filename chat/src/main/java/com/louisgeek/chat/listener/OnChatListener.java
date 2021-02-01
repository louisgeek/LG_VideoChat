package com.louisgeek.chat.listener;


import com.louisgeek.chat.model.ChatModel;

/**
 * Created by louisgeek on 2019/4/13.
 */
public interface OnChatListener {
    //========================== do =======================
    void doChatInvite(ChatModel chatModel);

    void doChatCancel(boolean isTimeout);

    //------------------
    void doChatAgree(ChatModel chatModel);

    void doChatReject();

    //------------------
    void doChatEnd();

    void doSwitchAudioVideo(boolean isVideo);

//    void doSwitchCameraVideoCapturer();

    //========================== on =======================
    void onChatInvite(ChatModel chatModel);

    void onChatCancel(boolean isTimeout);

    void onChatOffer();

    //------------------
    void onChatAgree(ChatModel chatModel);

    void onChatReject();

    void onChatAnswer();

    //------------------
    void onChatEnd();

    void onSwitchAudioVideo(boolean isVideo);

//    void onCameraStatusChange(String cameraStatus);


}
