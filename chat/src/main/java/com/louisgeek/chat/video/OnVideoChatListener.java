package com.louisgeek.chat.video;


/**
 * Created by louisgeek on 2019/4/13.
 */
public interface OnVideoChatListener {
    //========================== do =======================
    void doVideoChatInvite();

    void doVideoChatCancel(boolean isTimeout);

    //------------------
    void doVideoChatAgree();

    void doVideoChatReject();

    //------------------
    void doVideoChatEnd();

    void doSwitchAudioVideo(boolean isVideo);

//    void doSwitchCameraVideoCapturer();

    //========================== on =======================
    void onVideoChatInvite();

    void onVideoChatCancel(boolean isTimeout);

    void onVideoChatOffer();

    //------------------
    void onVideoChatAgree();

    void onVideoChatReject();

    void onVideoChatAnswer();

    //------------------
    void onVideoChatEnd();

    void onSwitchAudioVideo(boolean isVideo);

//    void onCameraStatusChange(String cameraStatus);
}
