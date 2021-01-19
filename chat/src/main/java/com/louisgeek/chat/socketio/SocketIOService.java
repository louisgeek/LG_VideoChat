package com.louisgeek.chat.socketio;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketIOService extends Service {
    private static final String TAG = "SocketService";
 /*   private static final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.e(TAG, "onServiceConnected: " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: " + name);
        }
    };*/
    private ScheduledExecutorService mScheduledExecutor;

  /*  public static void actionBind(Context context, String serviceUri, String serviceQuery) {
        Intent intent = new Intent(context, SocketIOService.class);
        intent.putExtra("serviceUri", serviceUri);
        intent.putExtra("serviceQuery", serviceQuery);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void actionUnbind(Context context) {
        context.unbindService(mServiceConnection);
    }
*/

    // 服务启动
    public static void actionStart(Context context, String serviceUri, String serviceQuery) {
        Log.d(TAG, "actionStart: ");
        Intent intent = new Intent(context, SocketIOService.class);
        intent.putExtra("serviceUri", serviceUri);
        intent.putExtra("serviceQuery", serviceQuery);
        context.startService(intent);
    }

    // 服务停止
    public static void actionStop(Context context) {
        Intent intent = new Intent(context, SocketIOService.class);
        context.stopService(intent);
        Log.d(TAG, "actionStop");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        String serviceUri = intent.getStringExtra("serviceUri");
        String serviceQuery = intent.getStringExtra("serviceQuery");
        Log.d(TAG, "onStartCommand serviceUri " + serviceUri + " serviceQuery " + serviceQuery);
        if (!TextUtils.isEmpty(serviceUri)) {
            //
            SocketIOManager.getInstance().init(serviceUri, serviceQuery);
//            startOuterHeart();
        }

        //        return super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    private void startOuterHeart() {
        //
        if (mScheduledExecutor == null) {
            mScheduledExecutor = Executors.newScheduledThreadPool(1);
        }
        mScheduledExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //异常会中断定时器
                try {
                    SocketIOManager.getInstance().heartBeat();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 40, 50, TimeUnit.SECONDS);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(SocketIOEvent event) {
        //no-op
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        //
        SocketIOManager.getInstance().release();
        //
        if (mScheduledExecutor != null) {
            mScheduledExecutor.shutdownNow();
            mScheduledExecutor = null;
        }
        super.onDestroy();
    }

    private class MyBinder extends Binder {
        public SocketIOService getService() {
            return SocketIOService.this;
        }
    }
}
