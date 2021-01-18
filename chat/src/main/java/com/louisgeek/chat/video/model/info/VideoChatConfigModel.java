package com.louisgeek.chat.video.model.info;


/**
 * inviteUserModel 邀请 otherUserModel
 */
public class VideoChatConfigModel {
    public boolean isVideoInitConfig = true;
    public int timeout = 10 * 1000;
    public String dateTime;
    //
    @Deprecated
    public String videoChatParameter;
}
