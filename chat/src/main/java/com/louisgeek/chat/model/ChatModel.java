package com.louisgeek.chat.model;

import java.io.Serializable;

public class ChatModel implements Serializable {
    public UserModel fromUserModel;
    public UserModel toUserModel;

    public boolean isVideo;
}
