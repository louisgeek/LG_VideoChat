package com.louisgeek.lg_videochat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.louisgeek.chat.socketio.SocketEvent;
import com.louisgeek.chat.socketio.SocketEvents;
import com.louisgeek.chat.video.CallVideoHelper;
import com.louisgeek.chat.video.OnVideoChatListener;
import com.louisgeek.chat.video.VideoChatFragment;
import com.louisgeek.chat.video.model.ChatInfoTypeModel;
import com.louisgeek.chat.video.model.base.ChatInfoModel;
import com.louisgeek.chat.video.model.info.VideoChatConfigModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private VideoChatFragment mVideoChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        EventBus.getDefault().register(this);
        //
        Button id_agree = findViewById(R.id.id_agree);
        Button id_reject = findViewById(R.id.id_reject);
        Button id_cancel = findViewById(R.id.id_cancel);
        Button id_end = findViewById(R.id.id_end);
        Button id_sw_av = findViewById(R.id.id_sw_av);
        id_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatAgree();
            }
        });
        id_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatReject();
            }
        });
        id_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatCancel(false);
            }
        });
        id_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoChatFragment.doVideoChatEnd();
            }
        });
        id_sw_av.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = false;//todo
                mVideoChatFragment.doSwitchAudioVideo(isVideo);
            }
        });

        String chatInfoModelJson = getIntent().getStringExtra("chatInfoModelJson");

        showChat(chatInfoModelJson);
    }

    public static void actionStart(Context context, String chatInfoModelJson) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatInfoModelJson", chatInfoModelJson);
        context.startActivity(intent);

    }

    private void showChat(String chatInfoModelJson) {
        mVideoChatFragment = VideoChatFragment.newInstance(chatInfoModelJson, "");
        mVideoChatFragment.addOnVideoChatListener(mOnVideoChatListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_frame_layout_container, mVideoChatFragment)
                .commitAllowingStateLoss();
    }

    private OnVideoChatListener mOnVideoChatListener = new OnVideoChatListener() {
        @Override
        public void doVideoChatInvite() {

        }

        @Override
        public void doVideoChatCancel(boolean isTimeout) {
            Toast.makeText(ChatActivity.this, "取消", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void doVideoChatAgree() {
            Toast.makeText(ChatActivity.this, "同意", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void doVideoChatReject() {
            Toast.makeText(ChatActivity.this, "拒绝", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void doVideoChatEnd() {
            Toast.makeText(ChatActivity.this, "挂断", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void doSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(ChatActivity.this, "切换到视频" + isVideo, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoChatInvite() {

        }

        @Override
        public void onVideoChatCancel(boolean isTimeout) {
            Toast.makeText(ChatActivity.this, "被取消", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onVideoChatOffer() {

        }

        @Override
        public void onVideoChatAgree() {
            Toast.makeText(ChatActivity.this, "被同意", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVideoChatReject() {
            Toast.makeText(ChatActivity.this, "被拒绝", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onVideoChatAnswer() {

        }

        @Override
        public void onVideoChatEnd() {
            Toast.makeText(ChatActivity.this, "被挂断", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onSwitchAudioVideo(boolean isVideo) {
            Toast.makeText(ChatActivity.this, "被切换到视频" + isVideo, Toast.LENGTH_SHORT).show();
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(SocketEvent socketEvent) {
        String json = socketEvent.json;
        String event = socketEvent.event;
        Log.e(TAG, "onSubscribe: event; " + event);
        if (SocketEvents.userChat.equals(event)) {
            ChatInfoModel chatInfoModelTemp = new Gson().fromJson(json, ChatInfoModel.class);
            String chatInfoType = chatInfoModelTemp.chatInfoType;
            if (ChatInfoTypeModel.ChatInfo_Invite.equals(chatInfoType)) {
                //
                String chatInfoModelJson = json;
                //
                ChatInfoModel<VideoChatConfigModel> videoChatConfigModelChatInfoModel = new Gson().fromJson(chatInfoModelJson, new TypeToken<ChatInfoModel<VideoChatConfigModel>>() {
                }.getType());
                //
                CallVideoHelper.userModel = videoChatConfigModelChatInfoModel.toUserModel;
                CallVideoHelper.otherUserModel = videoChatConfigModelChatInfoModel.fromUserModel;
                //
                showChat(chatInfoModelJson);

            } else {
                //其他消息 里面的页面处理
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}