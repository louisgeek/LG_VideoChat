package com.louisgeek.chat.model;


public class SdpInfoChatModel extends ChatModel {
    public String sdpMLineIndex;
    public String sdpMid;
    public String sdp;

    public SdpInfoChatModel(String sdpMLineIndex, String sdpMid, String sdp) {
        this.sdpMLineIndex = sdpMLineIndex;
        this.sdpMid = sdpMid;
        this.sdp = sdp;
    }
}
