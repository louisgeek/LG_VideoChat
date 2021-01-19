package com.louisgeek.chat.socketio;

public class SocketIOEvent {
    public String event;
    public String json;

    private SocketIOEvent(String event, String json) {
        this.event = event;
        this.json = json;
    }

    public static SocketIOEvent create(String event, String json) {
        return new SocketIOEvent(event, json);
    }
}
