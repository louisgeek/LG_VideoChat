package com.louisgeek.chat.video.model.info;


public class VideoChatInfoModel {
    public String sdpMLineIndex;
    public String sdpMid;
    public String sdp;

    public VideoChatInfoModel(String sdpMLineIndex, String sdpMid, String sdp) {
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
        this.sdp = sdp;
    }
}
