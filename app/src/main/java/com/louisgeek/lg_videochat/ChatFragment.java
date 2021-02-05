package com.louisgeek.lg_videochat;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;

import com.louisgeek.chat.base.BaseChatFragment;
import com.louisgeek.chat.helper.ChatUtil;
import com.louisgeek.chat.listener.OnChatListener;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.model.SdpTypeChatModel;
import com.louisgeek.chat.video.CameraVideoCapturerHelper;
import com.louisgeek.chat.video.VideoConstants;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends BaseChatFragment {

    private static final String TAG = "ChatFragment";
    //=====================================================================
    //
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected List<OnChatListener> mOnChatListenerList = new ArrayList<>();
    //
    private SurfaceViewRenderer id_svr_local;
    private SurfaceViewRenderer id_svr_remote;
    //
    private ChatModel mChatModel;
    private String mParam2;
    //
    private Button id_btn_reject;
    private Button id_btn_cancel_end;
    private Button id_btn_sw_v2a;
    //
    private Button id_btn_sw_video;
    private Button id_btn_sw_audio;
    private Button id_btn_sw_mk;
    private TextView id_tv_name;
    private Button id_btn_agree;
    private TextView id_tv_time;
    //
    private LinearLayout id_ll_title;
    private LinearLayout id_ll_btn_op_self;
    private LinearLayout id_ll_btn_op;
    //
    private CountDownTimer mCountDownTimer;

    //=========================================================================

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoChatFragment.
     */
    //
    public static ChatFragment newInstance(ChatModel chatModel, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, chatModel);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ChatModel setupChatModel() {
        return mChatModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mChatModel = (ChatModel) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.e(TAG, "onCreate: ");
//        String chatInfoModelJson = mParam1;
//        CallConfigModel = new Gson().fromJson(chatInfoModelJson, CallConfigModel.class);
    }


    public void addOnChatListener(OnChatListener onChatListener) {
        if (!mOnChatListenerList.contains(onChatListener)) {
            mOnChatListenerList.add(onChatListener);
        }
    }

    public void removeonChatListener(OnChatListener onChatListener) {
        mOnChatListenerList.remove(onChatListener);
    }

    //

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(rootLayout);
        return rootLayout;
    }

    private void initView(View rootLayout) {
        //
        id_svr_local = rootLayout.findViewById(R.id.id_svr_local);
        id_svr_remote = rootLayout.findViewById(R.id.id_svr_remote);
        //
        id_ll_title = rootLayout.findViewById(R.id.id_ll_title);
        id_ll_btn_op_self = rootLayout.findViewById(R.id.id_ll_btn_op_self);
        id_ll_btn_op = rootLayout.findViewById(R.id.id_ll_btn_op);
        //
        id_btn_agree = rootLayout.findViewById(R.id.id_btn_agree);
        id_btn_reject = rootLayout.findViewById(R.id.id_btn_reject);
        id_btn_cancel_end = rootLayout.findViewById(R.id.id_btn_cancel_end);
        id_btn_sw_v2a = rootLayout.findViewById(R.id.id_btn_sw_v2a);
        id_btn_sw_video = rootLayout.findViewById(R.id.id_btn_sw_video);
        id_btn_sw_audio = rootLayout.findViewById(R.id.id_btn_sw_audio);
        id_btn_sw_mk = rootLayout.findViewById(R.id.id_btn_sw_mk);
        id_tv_name = rootLayout.findViewById(R.id.id_tv_name);
        id_tv_time = rootLayout.findViewById(R.id.id_tv_time);
        //
        boolean isCaller = ObjectsCompat.equals(mChatModel.fromUserModel.userId, ChatUtil.userModel.userId);
        if (isCaller) {
            doChatInvite();
        } else {
            onChatInvite();
        }
        //
        id_btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChatAgree();
            }
        });
        id_btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChatReject();
            }
        });
        id_btn_cancel_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                onKeyBackPressed();
            }
        });
        if (mChatModel.isVideo) {
            id_btn_sw_v2a.setText("语音");
        } else {
            id_btn_sw_v2a.setText("视频");
        }
        id_btn_sw_v2a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isVideo = false;
                if ("视频".equals(id_btn_sw_v2a.getText().toString())) {
                    //切换到语音
                    isVideo = false;
                } else if ("语音".equals(id_btn_sw_v2a.getText().toString())) {
                    //切换到视频
                    isVideo = true;
                }
                //
                doSwitchAudioVideo(isVideo);

            }
        });

        id_btn_sw_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSwitchCameraVideoCapturer();

            }
        });
        //通话
     /*   MyAudioManger.get().setVoiceCallModeUseEarpiece();
        if (MyAudioManger.TYPE_VOICE_CALL_EARPIECE == MyAudioManger.get().getCurrentType()) {
            id_btn_sw_audio.setText("听筒");
        } else if (MyAudioManger.TYPE_VOICE_CALL_SPEAKER == MyAudioManger.get().getCurrentType()) {
            id_btn_sw_audio.setText("外放");
        } else {
            id_btn_sw_audio.setText("错误");
        }*/
        id_btn_sw_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换 外放/听筒
                doSwitchAudio();
            }
        });

      /*  if (MyAudioManger.get().isMicrophoneMute()) {
            id_btn_sw_mk.setText("静音");
        } else {
            id_btn_sw_mk.setText("非静");
        }*/
        id_btn_sw_mk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换 麦克风静音
                doSwitchMute();
            }
        });
    }

    private void switchCameraVideoCapturer() {
        baseSwitchCameraVideoCapturer();
        //返回 在 baseOnCameraStatusChange 会调里
    }

    private void switchAudioVideo(boolean isVideo) {
        if (isVideo) {
            id_btn_sw_v2a.setText("视频");
            id_svr_local.setVisibility(View.VISIBLE);
            id_svr_remote.setVisibility(View.VISIBLE);
            //
            id_ll_title.setVisibility(View.VISIBLE);
            id_ll_btn_op_self.setVisibility(View.VISIBLE);
            id_ll_btn_op.setVisibility(View.VISIBLE);
        } else {
            id_btn_sw_v2a.setText("语音");
            id_svr_local.setVisibility(View.GONE);
            id_svr_remote.setVisibility(View.GONE);
            //
            id_ll_title.setVisibility(View.GONE);
            id_ll_btn_op_self.setVisibility(View.GONE);
            id_ll_btn_op.setVisibility(View.GONE);
        }
        //调用内部的
        baseSwitchAudioVideo(isVideo);
    }

    @Override
    protected SurfaceViewRenderer setupLocalSurfaceViewRenderer() {
        return id_svr_local;
    }

    @Override
    protected SurfaceViewRenderer setupRemoteSurfaceViewRenderer() {
        return id_svr_remote;
    }

    @Override
    protected VideoCapturer setupLocalVideoCapturer() {
        //普通摄像头
        VideoCapturer localVideoCapturer = CameraVideoCapturerHelper.getFrontFacingCameraVideoCapturer(mContext);
        if (localVideoCapturer != null) {
            //前置一般 true
            VideoConstants.localVideoMirror = true;
        } else {
            //没有前置 后置
            localVideoCapturer = CameraVideoCapturerHelper.getBackFacingCameraVideoCapturer(mContext);
            VideoConstants.localVideoMirror = false;
        }
        return localVideoCapturer;
    }


    @Override
    public boolean onKeyBackPressed() {
        if ("取消".equals(id_btn_cancel_end.getText().toString())) {
            doChatCancel(false);
            return true;
        } else if ("挂断".equals(id_btn_cancel_end.getText().toString())) {
            doChatEnd();
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //========================do ====================
    public void doChatInvite() {
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doChatInvite(mChatModel);
        }
        //
        id_btn_reject.setVisibility(View.GONE);
        id_btn_agree.setVisibility(View.GONE);
        id_btn_cancel_end.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setText("取消");
        //
//        id_tv_name.setText("正在呼叫" + otherUserModel.userName);
    }

    public void doChatCancel(boolean isTimeout) {
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doChatCancel(isTimeout);
        }
        //
        ChatUtil.sendCancel(isTimeout);
        onBackUp();
    }

    public void doChatReject() {
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doChatReject();
        }
        //
        ChatUtil.sendReject();
        onBackUp();
    }

    public void doChatAgree() {
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doChatAgree(mChatModel);
        }
        //
        ChatUtil.sendAgree();
        //
        id_btn_reject.setVisibility(View.GONE);
        id_btn_agree.setVisibility(View.GONE);
        id_btn_cancel_end.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setText("挂断");
//        id_tv_name.setText("和" + otherUserModel.userName + "通话中");
    }

    public void doChatEnd() {
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doChatEnd();
        }
        //
        ChatUtil.sendChatEnd();
        onBackUp();
    }

    public void doSwitchAudioVideo(boolean isVideo) {
        switchAudioVideo(isVideo);
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.doSwitchAudioVideo(isVideo);
        }
        //
        ChatUtil.sendSwitchVideo(isVideo);
    }

    protected void doSwitchCameraVideoCapturer() {
        //
               /* for (onChatListener onChatListener : monChatListenerList) {
                    onChatListener.doSwitchCameraVideoCapturer();
                }*/

        //切换 摄像头
        switchCameraVideoCapturer();
    }

    public void doSwitchAudio() {
 /*       if (MyAudioManger.TYPE_VOICE_CALL_EARPIECE == MyAudioManger.get().getCurrentType()) {
                    MyAudioManger.get().setVoiceCallModeUseSpeaker();
                } else if (MyAudioManger.TYPE_VOICE_CALL_SPEAKER == MyAudioManger.get().getCurrentType()) {
                    MyAudioManger.get().setVoiceCallModeUseEarpiece();
                }
                //
                if (MyAudioManger.TYPE_VOICE_CALL_EARPIECE == MyAudioManger.get().getCurrentType()) {
                    id_btn_sw_audio.setText("听筒");
                } else if (MyAudioManger.TYPE_VOICE_CALL_SPEAKER == MyAudioManger.get().getCurrentType()) {
                    id_btn_sw_audio.setText("外放");
                } else {
                    id_btn_sw_audio.setText("错误");
                }*/
    }

    public void doSwitchMute() {
          /*  MyAudioManger.get().setMicrophoneMute(!MyAudioManger.get().isMicrophoneMute());
                //
                if (MyAudioManger.get().isMicrophoneMute()) {
                    id_btn_sw_mk.setText("静音");
                } else {
                    id_btn_sw_mk.setText("非静");
                }*/
    }

    //===============================================

    @Override
    protected void onChatInvite() {
        super.onChatInvite();
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onChatInvite(mChatModel);
        }
        //
        id_btn_reject.setVisibility(View.VISIBLE);
        id_btn_agree.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setVisibility(View.GONE);
        //
//        id_tv_name.setText(otherUserModel.userName + "邀请你通话");
    }

    protected void onChatCancel(boolean isTimeout) {
        super.onChatCancel(isTimeout);
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onChatCancel(isTimeout);
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "取消");
//        Toast.makeText(mActivity, "", Toast.LENGTH_SHORT).show();
        onBackUp();
    }

    protected void onChatAgree() {
        super.onChatAgree();
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onChatAgree(mChatModel);
        }
        //
        id_btn_reject.setVisibility(View.GONE);
        id_btn_agree.setVisibility(View.GONE);
        id_btn_cancel_end.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setText("挂断");
//        id_tv_name.setText("和" + otherUserModel.userName + "通话中");

    }

    protected void onChatReject() {
        super.onChatReject();
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onChatReject();
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "拒绝");
        onBackUp();

    }

    protected void onChatEnd() {
        super.onChatEnd();
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onChatEnd();
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "挂断");
        onBackUp();
    }

    protected void onChatOffer(SdpTypeChatModel sdpTypeChatModel) {
        super.onChatOffer(sdpTypeChatModel);
    }

    protected void onChatAnswer(SdpTypeChatModel sdpTypeChatModel) {
        super.onChatAnswer(sdpTypeChatModel);
    }

    @Override
    protected void onSwitchAudioVideo(boolean isVideo) {
        super.onSwitchAudioVideo(isVideo);
        //
        switchAudioVideo(isVideo);
        //
        for (OnChatListener onChatListener : mOnChatListenerList) {
            onChatListener.onSwitchAudioVideo(isVideo);
        }

    }

    @Override
    protected void onCameraSwitch(int code, String msg) {
        super.onCameraSwitch(code, msg);
        if (code == -1) {
            Toast.makeText(mContext, "摄像头切换失败" + msg, Toast.LENGTH_SHORT).show();
        } else if (code == 0) {
            Log.e(TAG, "onCameraSwitch:切换到后置摄像头");
        } else if (code == 1) {
            Log.e(TAG, "onCameraSwitch:切换到前置摄像头");
        }
    }

    @Override
    protected void baseOnCameraStatusChange(String cameraStatus) {
        super.baseOnCameraStatusChange(cameraStatus);
        //
       /* for (onChatListener onChatListener : monChatListenerList) {
            onChatListener.onCameraStatusChange(cameraStatus);
        }*/
        //
        if (CameraStatus_None.equals(cameraStatus)) {
            id_btn_sw_video.setText("无摄");
        } else if (CameraStatus_COMM.equals(cameraStatus)) {
            id_btn_sw_video.setText("通用");
        } else if (CameraStatus_FRONT.equals(cameraStatus)) {
            id_btn_sw_video.setText("前置");
        } else if (CameraStatus_BACK.equals(cameraStatus)) {
            id_btn_sw_video.setText("后置");
        }
    }

  /*  @Override
    protected void baseSwitchAudioVideo(boolean isVideo) {
        super.baseSwitchAudioVideo(isVideo);
        //
        id_ll_btn_op_self
    }*/

    private void onBackUp() {
      /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            getActivity().supportFinishAfterTransition();
        }*/
    }

}
