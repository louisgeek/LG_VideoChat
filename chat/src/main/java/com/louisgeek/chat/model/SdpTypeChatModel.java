package com.louisgeek.chat.model;


public class SdpTypeChatModel extends ChatModel {
    public String sdpType;
    public String description;

    public SdpTypeChatModel(String sdpType, String description) {
        this.sdpType = sdpType;
        this.description = description;
    }
}
