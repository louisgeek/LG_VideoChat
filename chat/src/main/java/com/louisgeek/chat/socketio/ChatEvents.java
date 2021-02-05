package com.louisgeek.chat.socketio;


import com.louisgeek.chat.socketio.listener.EventEmitterListener;

import java.util.HashMap;
import java.util.Map;

public class ChatEvents {
    public static final String check_online = "check_online";
    public static final String room_online = "room_online";
    //
    public static final String invite = "invite";
    public static final String cancel = "cancel";
    //
    public static final String agree = "agree";
    public static final String reject = "reject";

    public static final String end = "end";

    public static final String switchVideo2Audio = "switchVideo2Audio";
    //局部监听 视频通信界面
    public static final String offer = "offer";
    public static final String answer = "answer";
    public static final String candidate = "candidate";
    //
    public static final String heartBeat = "heartBeat";
    //
    public static final String userChat = "userChat";
    public static Map<String, EventEmitterListener> eventMap = new HashMap<>();

    static {
        eventMap.put(check_online, new EventEmitterListener(check_online));
        eventMap.put(room_online, new EventEmitterListener(room_online));
        //
        eventMap.put(invite, new EventEmitterListener(invite));
        eventMap.put(cancel, new EventEmitterListener(cancel));
        //
        eventMap.put(agree, new EventEmitterListener(agree));
        eventMap.put(reject, new EventEmitterListener(reject));
        //
        eventMap.put(end, new EventEmitterListener(end));
        //
        eventMap.put(offer, new EventEmitterListener(offer));
        eventMap.put(answer, new EventEmitterListener(answer));
        eventMap.put(candidate, new EventEmitterListener(candidate));
        //
        eventMap.put(heartBeat, new EventEmitterListener(heartBeat));
        //
        eventMap.put(userChat, new EventEmitterListener(userChat));

    }
}
