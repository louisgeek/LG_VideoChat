package com.louisgeek.chat.base;

import android.util.Log;

import com.google.gson.Gson;
import com.louisgeek.chat.helper.base.BaseUserChatHelper;
import com.louisgeek.chat.listener.SdpObserverListener;
import com.louisgeek.chat.model.info.VideoChatInfoModel;
import com.louisgeek.chat.model.info.VideoChatSdpInfoModel;

import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

/**
 * Created by louisgeek on 2019/10/22.
 */
public class BaseChatPeerSendHelper {
    private static final String TAG = "VideoChatSocketHelper";
    private final static Gson mGson = new Gson();


    /**
     *
     */
    public static void callerSendOffer(PeerConnection mPeerConnection, MediaConstraints mSdpMediaConstraints) {
        //
        mPeerConnection.createOffer(new SdpObserverListener() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                //
                String sdpType = sessionDescription.type.canonicalForm();
                mPeerConnection.setLocalDescription(this, sessionDescription);
                BaseUserChatHelper.sendOffer(new VideoChatSdpInfoModel(sdpType, sessionDescription.description));
            }
        }, mSdpMediaConstraints);
    }


    /**
     *
     */
    public static void calleeSendAnswer(PeerConnection mPeerConnection, MediaConstraints mSdpMediaConstraints) {
        //6 B 通过PC所提供的createAnswer()方法建立一个包含B 的SDP描述符answer信令
        mPeerConnection.createAnswer(new SdpObserverListener() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                //
                String sdpType = sessionDescription.type.canonicalForm();
                mPeerConnection.setLocalDescription(this, sessionDescription);
                BaseUserChatHelper.sendAnswer(new VideoChatSdpInfoModel(sdpType, sessionDescription.description));

            }
        }, mSdpMediaConstraints);
    }

    public static void calleeReceiveOffer(PeerConnection mPeerConnection, String description) {
        //4 B 通过服务器收到 A 的offer信令
        SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.OFFER, description);
        //5 B 将 A 的offer信令中所包含的的SDP描述符提取出来，通过PC所提供的setRemoteDescription()方法交给B 的PC实例
        mPeerConnection.setRemoteDescription(new SdpObserverListener() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e(TAG, "B setRemoteDescription onCreateSuccess: " + sessionDescription);
            }
        }, sessionDescription);
    }

    public static void callerReceiveAnswer(PeerConnection mPeerConnection, String description) {
        //9  A 通过服务器收到B 的answer信令
        SessionDescription sessionDescription = new SessionDescription(SessionDescription.Type.ANSWER, description);
        //10  A 接收到B 的answer信令后，将其中B 的SDP描述符提取出来，调用setRemoteDescripttion()方法交给 A 自己的PC实例
//        peerConnection.setRemoteDescription(new SimpleSdpObserver(), sessionDescription);
        mPeerConnection.setRemoteDescription(new SdpObserverListener() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.e(TAG, "A setRemoteDescription onCreateSuccess: " + sessionDescription);
            }
        }, sessionDescription);
    }

    public static void onReceiveCandidate(PeerConnection mPeerConnection, VideoChatInfoModel videoChatInfoModel) {
        String sdpMid = videoChatInfoModel.sdpMid;
        String _sdpMLineIndex = videoChatInfoModel.sdpMLineIndex;
        String sdp = videoChatInfoModel.sdp;
        //
        int sdpMLineIndex = Integer.parseInt(_sdpMLineIndex);
        IceCandidate remoteIceCandidate = new IceCandidate(sdpMid, sdpMLineIndex, sdp);
        mPeerConnection.addIceCandidate(remoteIceCandidate);
    }

}
