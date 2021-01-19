package com.louisgeek.chat.socketio;

import android.util.Log;

import com.louisgeek.chat.socketio.listener.EventEmitterListener;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class SocketIOManager {
    private static final String TAG = "SocketManager";
    private Socket mSocket;
    //    private String mQuery;
    private volatile boolean mSocketConnected;
    private volatile boolean mHeartBeatOnline;

    private SocketIOManager() {
    }

    public static SocketIOManager getInstance() {
        return Inner.INSTANCE;
    }

    public void init(String uri, String query) {
//        mQuery = query;
        //切换socket地址需要先断开连接
        release();
        //
        Log.e(TAG, "init: uri " + uri + " query " + query);
        try {
            IO.Options opts = new IO.Options();
//            opts.forceNew = true;
//            opts.reconnection = true;
            //重试次数 默认 Integer.MAX_VALUE
            opts.reconnectionAttempts = Integer.MAX_VALUE;
            //初始延迟 默认 1 秒 延迟按照 2 倍增长，受 randomizationFactor 因素影响 直到 reconnectionDelayMax
            opts.reconnectionDelay = 1000;
            //最大延迟 默认 5 秒
//            opts.reconnectionDelayMax = 5000;
            opts.reconnectionDelayMax = 3 * 60 * 1000;
//            opts.randomizationFactor = 0.5;
            opts.timeout = 40 * 1000;
//            opts.query = "param1=something&param2=another";
            opts.query = query == null ? "userId=something&param2=another" : query;
            mSocket = IO.socket(uri, opts);
            Log.e(TAG, "init: uu=== init 12 ");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (mSocket == null) {
            Log.e(TAG, "init: mSocket is null ");
            return;
        }
        //统一处理
        for (Map.Entry<String, EventEmitterListener> mapEntry : ChatEvents.eventMap.entrySet()) {
            String event = mapEntry.getKey();
            EventEmitterListener listener = mapEntry.getValue();
            mSocket.on(event, listener);
        }
        //
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_CONNECT " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = true;
                //
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_CONNECT, null));
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_CONNECT_ERROR " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                //
                mSocketConnected = false;
                //
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_CONNECT_ERROR, null));
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_CONNECT_TIMEOUT " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = false;
                //
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_CONNECT_TIMEOUT, null));
            }

        });
        mSocket.on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_CONNECTING " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                //
            }
        });
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_DISCONNECT " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = false;
                //
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_DISCONNECT, null));
            }

        });
        //
        mSocket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_ERROR " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = false;
                //
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_ERROR, null));
            }

        });
        mSocket.on(Socket.EVENT_PING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_PING " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                //
            }

        });
        mSocket.on(Socket.EVENT_PONG, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_PONG " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                //
            }

        });
        //
        mSocket.on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_RECONNECT " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
            }

        });
        mSocket.on(Socket.EVENT_RECONNECT_ATTEMPT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                //重连次数
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_RECONNECT_ATTEMPT " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
            }

        });
        mSocket.on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_RECONNECT_FAILED " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = false;
                EventBus.getDefault().post(SocketIOEvent.create(Socket.EVENT_RECONNECT_FAILED, null));
            }

        });
        mSocket.on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_RECONNECT_ERROR " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
                mSocketConnected = false;
            }

        });
        mSocket.on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e(TAG, "call: ========== start ============== ");
                for (Object arg : args) {
                    Log.e(TAG, "call: EVENT_RECONNECTING " + arg);
                }
                Log.e(TAG, "call: ========== end ============== ");
            }

        });
        //
        mSocket.connect();
    }

    public void release() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket = null;
        }
    }

    public Socket socket() {
        return mSocket;
    }

    public boolean isSocketConnected() {
        return mSocketConnected;
    }

    public boolean isHeartBeatOnline() {
        return mHeartBeatOnline;
    }

    public void heartBeat() {
        mSocket.emit("heartBeat", new Ack() {
            @Override
            public void call(Object... args) {
                String tag = (String) args[0];
                Log.e(TAG, "call: heartBeat  tag " + tag);
                mHeartBeatOnline = true;
            }
        });
    }


    public void sendMessage(final Object... obj) {
        mSocket.send(obj);
    }

    private static class Inner {
        private static final SocketIOManager INSTANCE = new SocketIOManager();
    }

}
