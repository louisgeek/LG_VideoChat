package com.louisgeek.chat.video.model.info;


public class VideoChatSdpInfoModel {
    public String sdpType;
    public String description;

    public VideoChatSdpInfoModel(String sdpType, String description) {
        this.sdpType = sdpType;
        this.description = description;
    }
}
