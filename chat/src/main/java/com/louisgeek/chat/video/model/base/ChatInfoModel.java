package com.louisgeek.chat.video.model.base;


/**
 * fromUserModel 指的是发送消息的人
 * toUserModel 指的是接收消息的人
 */
public class ChatInfoModel<T> {
    public UserModel fromUserModel;
    public UserModel toUserModel;
    //
    public String chatInfoType;
    public T chatInfo;
}
