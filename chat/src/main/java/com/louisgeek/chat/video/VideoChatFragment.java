package com.louisgeek.chat.video;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.ObjectsCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.louisgeek.chat.MessageSocketAdapter;
import com.louisgeek.chat.R;
import com.louisgeek.chat.video.model.base.ChatInfoModel;
import com.louisgeek.chat.video.model.info.VideoChatConfigModel;
import com.louisgeek.chat.video.model.info.VideoChatSdpInfoModel;

import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoChatFragment extends BaseVideoFragment {

    protected List<OnVideoChatListener> mOnVideoChatListenerList = new ArrayList<>();
    ChatInfoModel<VideoChatConfigModel> videoChatConfigModelChatInfoModel;
    //
    private SurfaceViewRenderer id_svr_local;

    public void addOnVideoChatListener(OnVideoChatListener onVideoChatListener) {
        if (!mOnVideoChatListenerList.contains(onVideoChatListener)) {
            mOnVideoChatListenerList.add(onVideoChatListener);
        }
    }

    private SurfaceViewRenderer id_svr_remote;

    //=====================================================================
    //
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //
    private String mParam1;
    private String mParam2;

    //
    public VideoChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoChatFragment.
     */
    //
    public static VideoChatFragment newInstance(String param1, String param2) {
        VideoChatFragment fragment = new VideoChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void removeOnVideoChatListener(OnVideoChatListener onVideoChatListener) {
        mOnVideoChatListenerList.remove(onVideoChatListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        String chatInfoModelJson = mParam1;
        videoChatConfigModelChatInfoModel = new Gson().fromJson(chatInfoModelJson, new TypeToken<ChatInfoModel<VideoChatConfigModel>>() {
        }.getType());
    }

    //=========================================================================


    private Button id_btn_agree;
    private Button id_btn_reject;
    private Button id_btn_cancel_end;
    private Button id_btn_sw_v2a;
    private Button id_btn_sw_video;
    private Button id_btn_sw_audio;
    private Button id_btn_sw_mk;
    private TextView id_tv_name;
    private TextView id_tv_time;
    private CountDownTimer mCountDownTimer;

    //


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View rootLayout = inflater.inflate(R.layout.fragment_video_chat, container, false);
        initView(rootLayout);
        return rootLayout;
    }

    private void initView(View rootLayout) {

        //
        id_svr_local = rootLayout.findViewById(R.id.id_svr_local);
        id_svr_remote = rootLayout.findViewById(R.id.id_svr_remote);
        //
        id_btn_agree = rootLayout.findViewById(R.id.id_btn_agree);
        id_btn_reject = rootLayout.findViewById(R.id.id_btn_reject);
        id_btn_cancel_end = rootLayout.findViewById(R.id.id_btn_cancel_end);
        id_btn_sw_v2a = rootLayout.findViewById(R.id.id_btn_sw_v2a);
        id_btn_sw_video = rootLayout.findViewById(R.id.id_btn_sw_video);
        id_btn_sw_audio = rootLayout.findViewById(R.id.id_btn_sw_audio);
        id_btn_sw_mk = rootLayout.findViewById(R.id.id_btn_sw_mk);
        //
        id_tv_name = rootLayout.findViewById(R.id.id_tv_name);
        id_tv_time = rootLayout.findViewById(R.id.id_tv_time);
        //
        //
        boolean isCaller = ObjectsCompat.equals(videoChatConfigModelChatInfoModel.fromUserModel.userId, CallVideoHelper.userModel.userId);
        if (isCaller) {
            for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
                onVideoChatListener.doVideoChatInvite();
            }
            //
            id_btn_reject.setVisibility(View.GONE);
            id_btn_agree.setVisibility(View.GONE);
            id_btn_cancel_end.setVisibility(View.VISIBLE);
            id_btn_cancel_end.setText("取消");
            //
            id_tv_name.setText("正在呼叫" + CallVideoHelper.otherUserModel.userName);

        } else {
            for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
                onVideoChatListener.onVideoChatInvite();
            }
            //
            id_btn_reject.setVisibility(View.VISIBLE);
            id_btn_agree.setVisibility(View.VISIBLE);
            id_btn_cancel_end.setVisibility(View.GONE);
            //
            id_tv_name.setText(CallVideoHelper.otherUserModel.userName + "邀请你通话");
        }
        //
        id_btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doVideoChatAgree();
            }
        });
        id_btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doVideoChatReject();
            }
        });
        id_btn_cancel_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                onKeyBackPressed();
            }
        });
        if (videoChatConfigModelChatInfoModel.chatInfo.isVideoInitConfig) {
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
        } else {
            id_btn_sw_v2a.setText("语音");
            id_svr_local.setVisibility(View.GONE);
            id_svr_remote.setVisibility(View.GONE);
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
    public boolean onKeyBackPressed() {
        if ("取消".equals(id_btn_cancel_end.getText().toString())) {
            doVideoChatCancel(false);
            return true;
        } else if ("挂断".equals(id_btn_cancel_end.getText().toString())) {
            doVideoChatEnd();
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //========================do ====================
   /* public void  doVideoChatInvite(){

    }*/
    public void doVideoChatCancel(boolean isTimeout) {
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.doVideoChatCancel(isTimeout);
        }
        //
        MessageSocketAdapter.sendCancel(isTimeout);
        onBackUp();
    }

    public void doVideoChatReject() {
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.doVideoChatReject();
        }
        //
        MessageSocketAdapter.sendReject();
        onBackUp();
    }

    public void doVideoChatAgree() {
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.doVideoChatAgree();
        }
        //
        MessageSocketAdapter.sendAgree();
        //
        id_btn_reject.setVisibility(View.GONE);
        id_btn_agree.setVisibility(View.GONE);
        id_btn_cancel_end.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setText("挂断");
        id_tv_name.setText("和" + CallVideoHelper.otherUserModel.userName + "通话中");
    }

    public void doVideoChatEnd() {
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.doVideoChatEnd();
        }
        //
        MessageSocketAdapter.sendChatEnd();
        onBackUp();
    }

    public void doSwitchAudioVideo(boolean isVideo) {
        switchAudioVideo(isVideo);
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.doSwitchAudioVideo(isVideo);
        }
        //
        MessageSocketAdapter.sendSwitchVideo(isVideo);
    }

    protected void doSwitchCameraVideoCapturer() {
        //
               /* for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
                    onVideoChatListener.doSwitchCameraVideoCapturer();
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
    protected void onVideoChatCancel(boolean isTimeout) {
        super.onVideoChatCancel(isTimeout);
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onVideoChatCancel(isTimeout);
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "取消");
//        Toast.makeText(mActivity, "", Toast.LENGTH_SHORT).show();
        onBackUp();
    }

    @Override
    protected void onVideoChatAgree() {
        super.onVideoChatAgree();
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onVideoChatAgree();
        }
        //
        id_btn_reject.setVisibility(View.GONE);
        id_btn_agree.setVisibility(View.GONE);
        id_btn_cancel_end.setVisibility(View.VISIBLE);
        id_btn_cancel_end.setText("挂断");
        id_tv_name.setText("和" + CallVideoHelper.otherUserModel.userName + "通话中");

    }

    @Override
    protected void onVideoChatReject() {
        super.onVideoChatReject();
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onVideoChatReject();
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "拒绝");
        onBackUp();

    }

    @Override
    protected void onVideoChatEnd() {
        super.onVideoChatEnd();
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onVideoChatEnd();
        }
        //
//        ToastManager.show("被" + CallVideoHelper.otherUserModel.userName + "挂断");
        onBackUp();
    }

    @Override
    protected void onVideoChatOffer(ChatInfoModel<VideoChatSdpInfoModel> chatInfoModel) {
        super.onVideoChatOffer(chatInfoModel);
    }

    @Override
    protected void onVideoChatAnswer(ChatInfoModel<VideoChatSdpInfoModel> chatInfoModel) {
        super.onVideoChatAnswer(chatInfoModel);
    }

    @Override
    protected void onSwitchAudioVideo(ChatInfoModel chatInfoModel, boolean isVideo) {
        super.onSwitchAudioVideo(chatInfoModel, isVideo);
        //
        switchAudioVideo(isVideo);
        //
        for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onSwitchAudioVideo(isVideo);
        }

    }

    @Override
    protected void baseOnCameraStatusChange(String cameraStatus) {
        super.baseOnCameraStatusChange(cameraStatus);
        //
       /* for (OnVideoChatListener onVideoChatListener : mOnVideoChatListenerList) {
            onVideoChatListener.onCameraStatusChange(cameraStatus);
        }*/
        //
        if (CallVideoHelper.CameraStatus_None.equals(cameraStatus)) {
            id_btn_sw_video.setText("无摄");
        } else if (CallVideoHelper.CameraStatus_COMM.equals(cameraStatus)) {
            id_btn_sw_video.setText("通用");
        } else if (CallVideoHelper.CameraStatus_FRONT.equals(cameraStatus)) {
            id_btn_sw_video.setText("前置");
        } else if (CallVideoHelper.CameraStatus_BACK.equals(cameraStatus)) {
            id_btn_sw_video.setText("后置");
        }
    }

    private void onBackUp() {
      /*  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        } else {
            getActivity().supportFinishAfterTransition();
        }*/
    }

}
