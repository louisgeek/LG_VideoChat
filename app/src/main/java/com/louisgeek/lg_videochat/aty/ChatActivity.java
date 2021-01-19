package com.louisgeek.lg_videochat.aty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.louisgeek.chat.ChatFragment;
import com.louisgeek.chat.listener.OnChatListener;
import com.louisgeek.lg_videochat.R;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private final OnChatListener mOnChatListener = new OnChatListener() {
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
    private ChatFragment mVideoChatFragment;

    public static void actionStart(Context context, String chatInfoModelJson) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("chatInfoModelJson", chatInfoModelJson);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        EventBus.getDefault().register(this);
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

        //直接打开
        mVideoChatFragment = ChatFragment.newInstance(chatInfoModelJson, "");
        mVideoChatFragment.addOnChatListener(mOnChatListener);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.id_frame_layout_container, mVideoChatFragment)
                .commitAllowingStateLoss();
    }

/*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(SocketEvent socketEvent) {
        String json = socketEvent.json;
        String event = socketEvent.event;
        Log.e(TAG, "onSubscribe: event; " + event);
        CallHelper.onInvite(socketEvent, new CallHelper.OnInviteBack() {
            @Override
            public void onInvite(String chatInfoModelJson) {
                //
                showChat(chatInfoModelJson);
            }
        });
    }
*/

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}