package com.louisgeek.chat.base;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.louisgeek.chat.helper.CallPlayerHelper;
import com.louisgeek.chat.helper.ChatUtil;
import com.louisgeek.chat.helper.PeerConnectionSendHelper;
import com.louisgeek.chat.listener.PeerConnectionObserverListener;
import com.louisgeek.chat.model.ChatModel;
import com.louisgeek.chat.model.InfoChatModel;
import com.louisgeek.chat.model.SdpInfoChatModel;
import com.louisgeek.chat.model.SdpTypeChatModel;
import com.louisgeek.chat.socketio.ChatEvents;
import com.louisgeek.chat.socketio.SocketIOEvent;
import com.louisgeek.chat.video.CameraVideoCapturerHelper;
import com.louisgeek.chat.video.PeerConnectionHelper;
import com.louisgeek.chat.video.VideoConstants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;


/**
 * Created by louisgeek on 2019/10/18.
 */
public abstract class BaseChatFragment extends Fragment {
    //
    public static final String CameraStatus_None = "CameraStatus_None";
    public static final String CameraStatus_COMM = "CameraStatus_COMM";
    public static final String CameraStatus_BACK = "CameraStatus_BACK";
    public static final String CameraStatus_FRONT = "CameraStatus_FRONT";
    private static final String TAG = "BaseChatFragment";
    private final Gson mGson = new Gson();
    private final int smallSize = 400;

    protected Activity mActivity;
    protected Context mContext;

    //
    public BaseChatFragment() {
        // Required empty public constructor
    }

    private boolean localIsSmall = false;
    //video
    //
    private EglBase mEglBase = EglBase.create();
    //
    private SurfaceViewRenderer mLocalSurfaceViewRenderer;
    private SurfaceViewRenderer mRemoteSurfaceViewRenderer;
    private String mCameraStatus;
    //1
    private MediaConstraints mSdpMediaConstraints;
    private MediaConstraints mAudioConstraints;
    //3 createPeerConnectionFactory
    private PeerConnectionFactory mPeerConnectionFactory;
    //4 mPeerConnectionFactory createAudioSource
    private AudioSource mLocalAudioSource;
    //5 mPeerConnectionFactory createAudioTrack
    private AudioTrack mLocalAudioTrack;
    //6 mPeerConnectionFactory createVideoSource
    private VideoSource mLocalVideoSource;
    //7 getCapturerObserver & SurfaceTextureHelper create
    private CapturerObserver mCapturerObserver;
    private SurfaceTextureHelper mSurfaceTextureHelper;
    //8 VideoCapturer initialize
    private VideoCapturer mLocalVideoCapturer;
    //9  mPeerConnectionFactory createVideoTrack & addSink
    private VideoTrack mLocalVideoTrack;
    //10 mPeerConnectionFactory createPeerConnection
    private PeerConnection mPeerConnection;
    //11 mPeerConnectionFactory createLocalMediaStream & addStream
//    private MediaStream mLocalMediaStream;
    private AudioManager mAudioManager;

    //
    protected abstract ChatModel setupChatModel();

    protected abstract SurfaceViewRenderer setupLocalSurfaceViewRenderer();

    protected abstract SurfaceViewRenderer setupRemoteSurfaceViewRenderer();

    public abstract boolean onKeyBackPressed();

    @Override
    public void onResume() {
        super.onResume();
        baseSwitchAudioVideo(setupChatModel().isVideo);
    }

    @Override
    public void onPause() {
        super.onPause();
        baseSwitchAudioVideo(setupChatModel().isVideo);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (Activity) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mAudioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        mChatInfoModel = setupChatInfoModel();
        super.onViewCreated(view, savedInstanceState);
        //
        initRenderer();
        initVideoChat();
//        Logging.enableLogToDebugOutput(Logging.Severity.LS_ERROR);
        Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE);
 /*       boolean isCaller = ObjectsCompat.equals(mChatInfoModel.fromUserModel.userId, CallVideoHelper.userModel.userId);
        if (isCaller) {
            //播放呼出铃声
            CallPlayerHelper.playCallInRing(getIsPlay());//暂时借用呼入声音
            //
//            onChatCallOut(mChatInfoModel.chatInfo);

        } else {
            //播放呼入铃声
            CallPlayerHelper.playCallInRing(getIsPlay());
            //
//            onChatCallIn(mChatInfoModel.chatInfo);
        }
*/
    }

    private void initRenderer() {
        mLocalSurfaceViewRenderer = setupLocalSurfaceViewRenderer();
        mRemoteSurfaceViewRenderer = setupRemoteSurfaceViewRenderer();
        //
        mLocalSurfaceViewRenderer.init(this.mEglBase.getEglBaseContext(), null);
        //后置摄像头一般设置成 false
        mLocalSurfaceViewRenderer.setMirror(!CameraStatus_BACK.equals(mCameraStatus));
        //视频画面缩放模式
//       自动适应屏幕比例， 画面存在被裁剪的可能
//        remoteSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        //自动适应画面比例，屏幕上可能存在黑边
//        remoteSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        //SCALE_ASPECT_FILL 和 SCALE_ASPECT_FIT 折中方案。
        mLocalSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        mLocalSurfaceViewRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                clickToggleRendererView();
            }
        });
        //
        mRemoteSurfaceViewRenderer.init(this.mEglBase.getEglBaseContext(), null);
//        mRemoteSurfaceViewRenderer.setMirror(true);
        //视频画面缩放模式
//       自动适应屏幕比例， 画面存在被裁剪的可能
//        remoteSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        //自动适应画面比例，屏幕上可能存在黑边
//        remoteSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        //SCALE_ASPECT_FILL 和 SCALE_ASPECT_FIT 折中方案。
        mRemoteSurfaceViewRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_BALANCED);
        mRemoteSurfaceViewRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                clickToggleRendererView();
            }
        });
        //初始化
        clickToggleRendererView();
    }


    private void clickToggleRendererView() {
        if (localIsSmall) {
            //放大本地
            ViewGroup.LayoutParams localLP = mLocalSurfaceViewRenderer.getLayoutParams();
            localLP.width = ViewGroup.LayoutParams.MATCH_PARENT;
            localLP.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mLocalSurfaceViewRenderer.setLayoutParams(localLP);
            //缩小远程
            ViewGroup.LayoutParams remoteLP = mRemoteSurfaceViewRenderer.getLayoutParams();
            remoteLP.width = smallSize;
            remoteLP.height = smallSize;
            mRemoteSurfaceViewRenderer.setLayoutParams(remoteLP);
            //
            ViewGroup viewGroup = (ViewGroup) mLocalSurfaceViewRenderer.getParent();
            viewGroup.removeView(mLocalSurfaceViewRenderer);
            viewGroup.removeView(mRemoteSurfaceViewRenderer);
            //重新add 解决覆盖问题
            viewGroup.addView(mLocalSurfaceViewRenderer);
            viewGroup.addView(mRemoteSurfaceViewRenderer);
//            mLocalSurfaceViewRenderer.setZOrderOnTop(false);
            //本地在下
            mLocalSurfaceViewRenderer.setZOrderMediaOverlay(false);
//            mRemoteSurfaceViewRenderer.setZOrderOnTop(true);
            //远程在上
            mRemoteSurfaceViewRenderer.setZOrderMediaOverlay(true);
            //
            localIsSmall = false;
        } else {
            //缩小本地
            ViewGroup.LayoutParams localLP = mLocalSurfaceViewRenderer.getLayoutParams();
            localLP.width = smallSize;
            localLP.height = smallSize;
            mLocalSurfaceViewRenderer.setLayoutParams(localLP);
            //放大远程
            ViewGroup.LayoutParams remoteLP = mRemoteSurfaceViewRenderer.getLayoutParams();
            remoteLP.width = ViewGroup.LayoutParams.MATCH_PARENT;
            remoteLP.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mRemoteSurfaceViewRenderer.setLayoutParams(remoteLP);
            //
            ViewGroup viewGroup = (ViewGroup) mLocalSurfaceViewRenderer.getParent();
            viewGroup.removeView(mLocalSurfaceViewRenderer);
            viewGroup.removeView(mRemoteSurfaceViewRenderer);
            //重新add 解决覆盖问题
            viewGroup.addView(mLocalSurfaceViewRenderer);
            viewGroup.addView(mRemoteSurfaceViewRenderer);
//            mLocalSurfaceViewRenderer.setZOrderOnTop(true);
            //本地在上
            mLocalSurfaceViewRenderer.setZOrderMediaOverlay(true);
//          remoteSurfaceViewRenderer.setZOrderOnTop(false);
            //远程在下
            mRemoteSurfaceViewRenderer.setZOrderMediaOverlay(false);
            //
            localIsSmall = true;
        }

    }

    private void initVideoChat() {
        //1 init Config
        mSdpMediaConstraints = new MediaConstraints();
        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
//        mSdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", ZApp.isVideo ? "true" : "false"));
        mSdpMediaConstraints.optional.add(new MediaConstraints.KeyValuePair(VideoConstants.DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
        //
        mAudioConstraints = new MediaConstraints();
        if (VideoConstants.noAudioProcessing) {
            Log.d(TAG, "Disabling audio processing");
           /* mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(VideoConstants.AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));
            mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(VideoConstants.AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(VideoConstants.AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair(VideoConstants.AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
      */
        }
        //音频约束
        //--回声消除
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation2", "true"));
        //--自动增益
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "false"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl2", "false"));
        //--噪音处理
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression2", "true"));
        //--回声消除2
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("echoCancellation", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"));
        //
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAudioMirroring", "false"));
        //视频约束
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseReduction", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googLeakyBucket", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googTemporalLayeredScreencast", "true"));
        //声活性检测
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("VoiceActivityDetection", "true"));
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("levelControl", "true"));

        //2 PeerConnectionFactory initialize initializationOptions
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions
                        .builder(requireContext().getApplicationContext())
                        //setFieldTrials
                        .setFieldTrials(VideoConstants.getFieldTrials())
                        .setEnableInternalTracer(true)
//                        .setEnableVideoHwAcceleration(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);
        //3 createPeerConnectionFactory
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        //视频模式
        //VideoEncoderFactory
        VideoEncoderFactory videoEncoderFactory;
        //VideoDecoderFactory
        VideoDecoderFactory videoDecoderFactory;
        if (VideoConstants.videoCodecHwAcceleration) {
            videoEncoderFactory = new DefaultVideoEncoderFactory(mEglBase.getEglBaseContext()
                    , VideoConstants.enableIntelVp8Encoder,
                    VideoConstants.enableH264HighProfile);
            //VideoDecoderFactory
            videoDecoderFactory = new DefaultVideoDecoderFactory(mEglBase.getEglBaseContext());
//            videoDecoderFactory = new HardwareVideoDecoderFactory(mEglBase.getEglBaseContext());
        } else {
            videoEncoderFactory = new SoftwareVideoEncoderFactory();
            videoDecoderFactory = new SoftwareVideoDecoderFactory();
        }
        //音频模式
      /*  final AudioDeviceModule audioDeviceModule = VideoConstants.useLegacyAudioDevice
                ? createLegacyAudioDevice()
                : createJavaAudioDevice();*/
        final AudioDeviceModule audioDeviceModule = PeerConnectionHelper.createJavaAudioDevice(mContext);
//        mPeerConnectionFactory = new PeerConnectionFactory(options, videoEncoderFactory, videoDecoderFactory);
        //PeerConnectionFactory.Builder
        PeerConnectionFactory.Builder builder = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(videoEncoderFactory)
                .setVideoDecoderFactory(videoDecoderFactory)
                .setAudioDeviceModule(audioDeviceModule)
//                .setAudioDeviceModule(null)
                .setOptions(options);
        mPeerConnectionFactory = builder.createPeerConnectionFactory();
        //4 mPeerConnectionFactory createAudioSource
        mLocalAudioSource = mPeerConnectionFactory.createAudioSource(mAudioConstraints);
        //5 mPeerConnectionFactory createAudioTrack
        mLocalAudioTrack = mPeerConnectionFactory.createAudioTrack(VideoConstants.AUDIO_TRACK_ID, mLocalAudioSource);
//        mLocalAudioTrack.setEnabled(true);
        mLocalAudioTrack.setEnabled(VideoConstants.enableAudio);
        //
        //6 mPeerConnectionFactory createVideoSource
        mLocalVideoSource = mPeerConnectionFactory.createVideoSource(false);
        //7 getCapturerObserver & SurfaceTextureHelper create
        mCapturerObserver = mLocalVideoSource.getCapturerObserver();
        mSurfaceTextureHelper = SurfaceTextureHelper.create(VideoConstants.VIDEO_CAPTURER_THREAD, mEglBase.getEglBaseContext());
        //8 VideoCapturer initialize
        /*if (DeviceHelper.isShine()) {
            //神州视翰
            mLocalVideoCapturer = MyCameraVideoCapturerHelper.getFrontFacingCameraVideoCapturer(mContext);
            if (mLocalVideoCapturer != null) {
                mCameraStatus = CallVideoHelper.CameraStatus_FRONT;
            } else {
                //没有前置
                mLocalVideoCapturer = MyCameraVideoCapturerHelper.getBackFacingCameraVideoCapturer(mContext);
                if (mLocalVideoCapturer != null) {
                    mCameraStatus = CallVideoHelper.CameraStatus_BACK;
                }
            }
//        } else if ("softwinner".equals(Build.MANUFACTURER) && "v902".equals(Build.MODEL)) {
        } else if (DeviceHelper.isDnake_bed_nurse()) {
            //狄耐克 护士站
            mLocalVideoCapturer = MyCameraVideoCapturerHelper.getFrontFacingCameraVideoCapturer(mContext);
            if (mLocalVideoCapturer != null) {
                mCameraStatus = CallVideoHelper.CameraStatus_FRONT;
            } else {
                //没有前置
                mLocalVideoCapturer = MyCameraVideoCapturerHelper.getBackFacingCameraVideoCapturer(mContext);
                if (mLocalVideoCapturer != null) {
                    mCameraStatus = CallVideoHelper.CameraStatus_BACK;
                }
            }
            Log.d(TAG, "PP====dnk  initVideoChat: mCameraStatus=" + mCameraStatus);

        } else if (UVCCameraHelper.hasUVCCamera(mContext) && !DeviceHelper.isDnake_smart_door()) {
            mLocalVideoCapturer = new UsbCameraVideoCapturer(mContext, mLocalSurfaceViewRenderer);
            if (mLocalVideoCapturer != null) {
                // TODO: 2019/10/23
                mCameraStatus = CallVideoHelper.CameraStatus_COMM;
            }
            Log.e(TAG, "initVideoChat: usb camera " + mCameraStatus);
        } else if (DeviceHelper.isDnake_smart_door()) {
            //部分智能终端调用前置直接报错 这里先直接调用后置
            mLocalVideoCapturer = CameraVideoCapturerHelper.getBackFacingCameraVideoCapturer(mContext);
            if (mLocalVideoCapturer != null) {
                mCameraStatus = CallVideoHelper.CameraStatus_BACK;
            }
        }else*/
        {
            //普通摄像头
            mLocalVideoCapturer = CameraVideoCapturerHelper.getFrontFacingCameraVideoCapturer(mContext);
            mLocalVideoCapturer = null;// FIXME: 2021/2/1
            if (mLocalVideoCapturer != null) {
                mCameraStatus = CameraStatus_FRONT;
            } else {
                //没有前置
                mLocalVideoCapturer = CameraVideoCapturerHelper.getBackFacingCameraVideoCapturer(mContext);
                if (mLocalVideoCapturer != null) {
                    mCameraStatus = CameraStatus_BACK;
                }
            }
            //
        }
        baseOnCameraStatusChange(mCameraStatus);
        if (mLocalVideoCapturer != null) {
            mLocalVideoCapturer.initialize(mSurfaceTextureHelper, mContext.getApplicationContext(), mCapturerObserver);
        }
        //9 mPeerConnectionFactory createVideoTrack & addSink
        mLocalVideoTrack = mPeerConnectionFactory.createVideoTrack(VideoConstants.VIDEO_TRACK_ID, mLocalVideoSource);
//        mLocalVideoTrack.setEnabled(true);
        mLocalVideoTrack.setEnabled(VideoConstants.videoCallEnabled);
        mLocalVideoTrack.addSink(this.mLocalSurfaceViewRenderer);
        //
        //10 mPeerConnectionFactory createPeerConnection 可以延后
        PeerConnection.RTCConfiguration rtcConfig = PeerConnectionHelper.getRTCConfiguration();
        mPeerConnection = mPeerConnectionFactory.createPeerConnection(rtcConfig,
                new PeerConnectionObserverListener() {
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        //
                        ChatUtil.sendCandidate(new SdpInfoChatModel(
                                String.valueOf(iceCandidate.sdpMLineIndex),
                                iceCandidate.sdpMid,
                                iceCandidate.sdp));
                    }

                    @Override
                    public void onAddRemoteVideoTrack(VideoTrack remoteVideoTrack) {
                        //
                        if (mActivity == null) {
                            return;
                        }
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ////UI线程执行
                                //构建远端view
                                SurfaceViewRenderer remoteSurfaceViewRenderer = mRemoteSurfaceViewRenderer;
//                                remoteSurfaceViewRenderer.setVisibility(View.VISIBLE);
                                //控件布局
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT);
//                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(360, 360);
//                            layoutParams.topMargin = 20;
//                            id_ll_remote.addView(remoteSurfaceViewRenderer, layoutParams);
//@@            setupRemoteSurfaceViewRendererLayout().addView(remoteSurfaceViewRenderer, layoutParams);
//@@             mRemoteSurfaceViewRendererList.add(remoteSurfaceViewRenderer);
                                //添加数据
                                //VideoTrack videoTrack = mediaStream.videoTracks.get(0);

                                remoteVideoTrack.addSink(remoteSurfaceViewRenderer);
                            }
                        });
                        //
                    }
                });
        //11 mPeerConnectionFactory createLocalMediaStream & add
//        ！！！注意 和 PeerConnectionObserverListener onAddStream 之间的关系
//        mLocalMediaStream = mPeerConnectionFactory.createLocalMediaStream("102");
//        mLocalMediaStream.addTrack(mLocalAudioTrack);
//        mLocalMediaStream.addTrack(mLocalVideoTrack);
//        mPeerConnection.addStream(mLocalMediaStream);
        //！！！注意 和 PeerConnectionObserverListener onAddTrack 之间的关系
        mPeerConnection.addTrack(mLocalAudioTrack);
        mPeerConnection.addTrack(mLocalVideoTrack);
    }

    @Override
    public void onDestroy() {
        release();
        //
        CallPlayerHelper.release();
        //
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
        mAudioManager.setSpeakerphoneOn(false);
//        ToastManager.show("切到 Normal 模式，关闭外放");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void release() {
        if (mPeerConnection != null) {
            mPeerConnection.dispose();
            mPeerConnection = null;
        }
        //
        if (mLocalAudioSource != null) {
            mLocalAudioSource.dispose();
            mLocalAudioSource = null;
        }
        if (mLocalVideoSource != null) {
            mLocalVideoSource.dispose();
            mLocalVideoSource = null;
        }
        if (mLocalAudioTrack != null) {
            try {
                mLocalAudioTrack.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocalAudioTrack = null;
        }
        if (mLocalVideoTrack != null) {
            try {
                mLocalVideoTrack.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocalVideoTrack = null;
        }
        if (mEglBase != null) {
            mEglBase.release();
            mEglBase = null;
        }
        if (mLocalSurfaceViewRenderer != null) {
            mLocalSurfaceViewRenderer.clearImage();
            mLocalSurfaceViewRenderer.release();
            mLocalSurfaceViewRenderer = null;
        }
        if (mRemoteSurfaceViewRenderer != null) {
            mRemoteSurfaceViewRenderer.clearImage();
            mRemoteSurfaceViewRenderer.release();
            mRemoteSurfaceViewRenderer = null;
        }
        //
        if (mLocalVideoCapturer != null) {
            try {
                mLocalVideoCapturer.stopCapture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLocalVideoCapturer.dispose();
            mLocalVideoCapturer = null;
        }
        if (mSurfaceTextureHelper != null) {
            mSurfaceTextureHelper.dispose();
            mSurfaceTextureHelper = null;
        }
        if (mPeerConnectionFactory != null) {
            mPeerConnectionFactory.dispose();
            mPeerConnectionFactory = null;
        }
        PeerConnectionFactory.stopInternalTracingCapture();
        PeerConnectionFactory.shutdownInternalTracer();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(SocketIOEvent socketIOEvent) {
        String json = socketIOEvent.json;
        String event = socketIOEvent.event;
        Log.e(TAG, "onSubscribe: event " + event + " " + json);
        if (ChatEvents.invite.equals(event)) {
            //被邀请的消息需要在外面处理,邀请之前此页面未打开，所以这里收不到
        } else if (ChatEvents.cancel.equals(event)) {
            InfoChatModel infoChatModel = mGson.fromJson(json, InfoChatModel.class);
            boolean isTimeout = Boolean.parseBoolean(infoChatModel.info);
            onChatCancel(isTimeout);
        } else if (ChatEvents.agree.equals(event)) {
            onChatAgree();
        } else if (ChatEvents.reject.equals(event)) {
            onChatReject();
        } else if (ChatEvents.end.equals(event)) {
            onChatEnd();
        } else if (ChatEvents.switchVideo2Audio.equals(event)) {
            InfoChatModel infoChatModel = mGson.fromJson(json, InfoChatModel.class);
            boolean isVideo = Boolean.parseBoolean(infoChatModel.info);
            //收到切换音视频信息
//            onSwitchAudioVideo(isVideo);
        } else if (ChatEvents.offer.equals(event)) {
            SdpTypeChatModel sdpTypeChatModel = mGson.fromJson(json, SdpTypeChatModel.class);
            onChatOffer(sdpTypeChatModel);
        } else if (ChatEvents.answer.equals(event)) {
            SdpTypeChatModel sdpTypeChatModel = mGson.fromJson(json, SdpTypeChatModel.class);
            onChatAnswer(sdpTypeChatModel);
        } else if (ChatEvents.candidate.equals(event)) {
            SdpInfoChatModel sdpInfoChatModel = mGson.fromJson(json, SdpInfoChatModel.class);
            onCandidate(sdpInfoChatModel);
        }
    }


    /**
     * =================== 其他  protected ===============
     */


    protected void baseSwitchAudioVideo(boolean isVideo) {
        if (isVideo) {
            CameraVideoCapturerHelper.startCameraVideoCapturer(mLocalVideoCapturer);
//            mLocalSurfaceViewRenderer.setVisibility(View.VISIBLE);
//            mRemoteSurfaceViewRenderer.setVisibility(View.VISIBLE);
        } else {
            CameraVideoCapturerHelper.stopCameraVideoCapturer(mLocalVideoCapturer);
//            mLocalSurfaceViewRenderer.setVisibility(View.GONE);
//            mRemoteSurfaceViewRenderer.setVisibility(View.GONE);
        }
    }

    protected void baseSwitchCameraVideoCapturer() {
        //
        if (mLocalVideoCapturer instanceof CameraVideoCapturer) {
            CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) mLocalVideoCapturer;
            CameraVideoCapturerHelper.switchCameraVideoCapturer(cameraVideoCapturer,
                    new CameraVideoCapturer.CameraSwitchHandler() {
                        @Override
                        public void onCameraSwitchDone(boolean isFrontFacing) {
                            //
                            if (isFrontFacing) {
                                //切换到前置
                                mCameraStatus = CameraStatus_FRONT;
                            } else {
                                //切换到后置
                                mCameraStatus = CameraStatus_BACK;
                            }

                        }

                        @Override
                        public void onCameraSwitchError(String error) {
                            //切换失败 一般停留在原来界面
                            Toast.makeText(mContext, "摄像头切换失败：" + error, Toast.LENGTH_SHORT).show();
                        }
                    });
            //
            baseOnCameraStatusChange(mCameraStatus);
        }
    }

    //============= on ==============

    //============= 被呼叫者操作 ==============

    protected void onChatInvite() {

    }

    protected void onChatCancel(boolean isTimeout) {

    }

    protected void onChatAgree() {
        //发送 offer
        PeerConnectionSendHelper.callerSendOffer(mPeerConnection, mSdpMediaConstraints);
    }

    protected void onChatReject() {

    }

    protected void onChatOffer(SdpTypeChatModel sdpTypeChatModel) {
        String description = sdpTypeChatModel.description;
        PeerConnectionSendHelper.calleeReceiveOffer(mPeerConnection, description);
        //
        PeerConnectionSendHelper.calleeSendAnswer(mPeerConnection, mSdpMediaConstraints);
        //停止铃声播放
        CallPlayerHelper.release();
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setSpeakerphoneOn(false);
//        ToastManager.show("切到 Call 模式，关闭外放");
        //被呼叫端 显示挂断
      /*  mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
            }
        });*/
    }

    protected void onChatAnswer(SdpTypeChatModel sdpTypeChatModel) {
        String description = sdpTypeChatModel.description;
        //
        PeerConnectionSendHelper.callerReceiveAnswer(mPeerConnection, description);
        //停止铃声播放
        CallPlayerHelper.release();
        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setSpeakerphoneOn(false);
//        ToastManager.show("切到 Call 模式，关闭外放");
        //呼叫端 显示挂断
     /*   mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
            }
        });*/

    }

    // ============= 双方操作 ==============
    //被挂断
    protected void onChatEnd() {

    }

    //被切换音频
    protected void onSwitchAudioVideo(boolean isVideo) {
        //
        baseSwitchAudioVideo(isVideo);
    }


    //摄像头状态改变
    protected void baseOnCameraStatusChange(String cameraStatus) {
        //
        Log.e(TAG, "baseOnCameraStatusChange: " + cameraStatus);
    }


    /**
     * ================================ 其他 private ===================================
     */


    private void onCandidate(SdpInfoChatModel sdpInfoChatModel) {
        //offer 回调发送的情况
        // B do【A打给B，B收到A的candidate】
        //answer 回调发送的情况
        // A do【A打给B，A收到B的candidate】
        String sdpMid = sdpInfoChatModel.sdpMid;
        String _sdpMLineIndex = sdpInfoChatModel.sdpMLineIndex;
        String sdp = sdpInfoChatModel.sdp;
        //
        int sdpMLineIndex = Integer.parseInt(_sdpMLineIndex);
        IceCandidate remoteIceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, sdp);
        mPeerConnection.addIceCandidate(remoteIceCandidate);
        Log.e(TAG, "dealOnCandidate: ");
    }

}
