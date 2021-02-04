package com.louisgeek.lg_videochat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.ObjectsCompat;

import com.louisgeek.chat.helper.ChatHelper;
import com.louisgeek.chat.listener.OnChatListener;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.model.UserModel;
import com.louisgeek.chat.socketio.SocketIOEvent;
import com.louisgeek.chat.socketio.SocketIOService;
import com.louisgeek.lg_videochat.adapter.MyUserListAdapter;
import com.louisgeek.lg_videochat.dialog.ChatDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

/**
 * 该页面 常驻 负责收发消息
 * //1
 * UserListActivity -> ChatDialogFragment => VideoChatFragment
 * //2
 * UserListActivity -> ChatActivity -> ChatFragment(可选) => VideoChatFragment
 */
public class UserListActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";

    private Context mContext;
    private MyUserListAdapter mMyUserListAdapter;
    private final OnChatListener onChatListener = new OnChatListener() {


        @Override
        public void doChatInvite(ChatModel chatModel) {

        }

        @Override
        public void doChatCancel(boolean isTimeout) {

        }

        @Override
        public void doChatAgree(ChatModel chatModel) {

        }

        @Override
        public void doChatReject() {

        }

        @Override
        public void doChatEnd() {

        }

        @Override
        public void doSwitchAudioVideo(boolean isVideo) {

        }

        @Override
        public void onChatInvite(ChatModel chatModel) {

        }

        @Override
        public void onChatCancel(boolean isTimeout) {

        }

        @Override
        public void onChatOffer() {

        }

        @Override
        public void onChatAgree(ChatModel chatModel) {

        }

        @Override
        public void onChatReject() {

        }

        @Override
        public void onChatAnswer() {

        }

        @Override
        public void onChatEnd() {

        }


        @Override
        public void onSwitchAudioVideo(boolean isVideo) {

        }


    };
    private String userName;
    //    private VideoChatFragment mVideoChatFragment;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        mContext = this;
        EventBus.getDefault().register(this);
        initView(savedInstanceState);

    }

    public static void actionStart(Context context, String userId, String userName) {
        Intent intent = new Intent(context, UserListActivity.class);
        intent.putExtra(SettingUsernameActivity.USER_ID, userId);
        intent.putExtra(SettingUsernameActivity.USER_NAME, userName);
        context.startActivity(intent);
    }

    private void initView(Bundle savedInstanceState) {

        TextView id_tv = findViewById(R.id.id_tv);
        ListView id_lv = findViewById(R.id.id_lv);

        mMyUserListAdapter = new MyUserListAdapter();
        id_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserModel otherUserModel = (UserModel) mMyUserListAdapter.getItem(position);

                if (ObjectsCompat.equals(otherUserModel.userId, userId)) {
//                    ToastManagerHelper.show("不能邀请自己");
                    Toast.makeText(mContext, "不能邀请自己", Toast.LENGTH_SHORT).show();
                    return;
                }
                //!!!
                UserModel userModel = new UserModel();
                userModel.userId = userId;
                userModel.userName = userName;
                //
                ChatModel chatModel = new ChatModel();
                chatModel.fromUserModel = userModel;
                chatModel.toUserModel = otherUserModel;
                chatModel.isVideo = true;
                //
                ChatHelper.doInvite(mContext, chatModel, new ChatHelper.DoInviteBack() {
                    @Override
                    public void doInvite(ChatModel chatModel) {
                        //type 1
                        showChatDialog(chatModel);
                        //type 2
//                        ChatActivity.actionStart(mContext, chatInfoModelJson);
                    }

                    @Override
                    public void selfOffline() {

                    }

                });
            }


        });
        id_lv.setAdapter(mMyUserListAdapter);

        //获取列表数据
        initData();
        //
        userId = getIntent().getStringExtra(SettingUsernameActivity.USER_ID);
        userName = getIntent().getStringExtra(SettingUsernameActivity.USER_NAME);


        //
        id_tv.setText(userId + " " + userName);
        //房间
        String roomId = "room_1031";
        //
        String query = String.format(Locale.CHINA, "userId=%s&userName=%s&roomId=%s", userId, userName, roomId);
//        query = UrlEscapers.urlFragmentEscaper().escape(query);
//        SocketManager.getInstance().init("192.168.12.222:3004", query);
//        SocketManager.getInstance().init(CommonConstant.getBaseUrlSocket(), query);
//        SocketService.actionStart(this, "http://192.168.12.222:3004", query);
//        SocketIOService.actionStart(this, "http://192.168.1.14:3004", query);
//        SocketIOService.actionStart(mContext, "http://192.168.12.222:3003", query);
        SocketIOService.actionStart(mContext, "http://192.168.1.12:3003", query);
    }

    private void initData() {
        UserModel userModel = new UserModel();
        userModel.userId = "6_1001";
        userModel.userName = "1001床患者";
        mMyUserListAdapter.addData(userModel);
        UserModel userModel2 = new UserModel();
        userModel2.userId = "6_1002";
        userModel2.userName = "1002床患者";
        mMyUserListAdapter.addData(userModel2);
        UserModel userModel3 = new UserModel();
        userModel3.userId = "6_1003";
        userModel3.userName = "1003床患者";
        mMyUserListAdapter.addData(userModel3);
        UserModel userModel4 = new UserModel();
        userModel4.userId = "2_1031";
        userModel4.userName = "一病区护士站";
        mMyUserListAdapter.addData(userModel4);
    }

  /*  @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (mVideoChatFragment != null) {
            mVideoChatFragment.onKeyBackPressed();
        }
    }*/

    /*public void onBackUp() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            supportFinishAfterTransition();
        }
    }*/

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        SocketIOService.actionStop(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(SocketIOEvent socketIOEvent) {
        String json = socketIOEvent.json;
        String event = socketIOEvent.event;
        Log.e(TAG, "onSubscribe: event " + event);
        ChatHelper.onInvite(socketIOEvent, new ChatHelper.OnInviteBack() {
            @Override
            public void onInvite(ChatModel chatModel) {
                //type 1
                showChatDialog(chatModel);
                //type 2
//                ChatActivity.actionStart(mContext, chatInfoModelJson);
            }

        });

    }

    private void showChatDialog(ChatModel chatModel) {
        ChatDialogFragment.newInstance(chatModel, "")
                .show(getSupportFragmentManager(), "showChatDialog");
    }
}