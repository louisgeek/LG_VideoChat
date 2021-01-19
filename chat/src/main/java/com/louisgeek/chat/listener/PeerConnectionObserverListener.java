package com.louisgeek.chat.listener;

import android.util.Log;

import com.louisgeek.chat.video.VideoConstants;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.VideoTrack;

/**
 * Created by louisgeek on 2019/8/8.
 */
public abstract class PeerConnectionObserverListener implements PeerConnection.Observer {
    private static final String TAG = "PeerConnectionObserverL";

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.e(TAG, "onSignalingChange: " + signalingState);
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.e(TAG, "onIceConnectionChange: " + iceConnectionState);
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.e(TAG, "onIceConnectionReceivingChange: " + b);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.e(TAG, "onIceGatheringChange: " + iceGatheringState);
    }


    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.e(TAG, "onIceCandidatesRemoved: " + iceCandidates);
    }

    /**
     * org.webrtc:google-webrtc:1.0.28513  报错
     * # AddStream is not available with Unified Plan SdpSemantics. Please use AddTrack instead.
     *
     * @param mediaStream
     */
    @Override
    public void onAddStream(MediaStream mediaStream) {
        //
        Log.e(TAG, "onAddStream: " + mediaStream);
        //
          /*  for (AudioTrack audioTrack : mediaStream.audioTracks) {
    //            audioTrack.setEnabled(true);
                audioTrack.setEnabled(VideoConstants.enableAudio);
            }
            //
            if (mediaStream.videoTracks.size() > 0) {
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                this.onAddRemoteVideoTrack(remoteVideoTrack);
                Log.e(TAG, "onAddTrack: onAddRemoteVideoTrack " + remoteVideoTrack);
            } else {
                Log.e(TAG, "onAddTrack: videoTracks size is 0 ");
            }*/
        //
    }


    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.e(TAG, "onRemoveStream: " + mediaStream);
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.e(TAG, "onDataChannel: " + dataChannel);
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.e(TAG, "onRenegotiationNeeded: ");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.e(TAG, "onAddTrack: " + mediaStreams);
        //org.webrtc:google-webrtc:1.0.21770 不用这个
        //
        for (int i = 0; i < mediaStreams.length; i++) {
            for (AudioTrack audioTrack : mediaStreams[i].audioTracks) {
                //                audioTrack.setEnabled(true);
                audioTrack.setEnabled(VideoConstants.enableAudio);
            }
        }
        //
        MediaStreamTrack mediaStreamTrack = rtpReceiver.track();
        if (mediaStreamTrack instanceof VideoTrack) {
            VideoTrack remoteVideoTrack = (VideoTrack) mediaStreamTrack;
            onAddRemoteVideoTrack(remoteVideoTrack);
            Log.e(TAG, "onAddTrack: onAddRemoteVideoTrack");
        }
        //
    }


    public abstract void onAddRemoteVideoTrack(VideoTrack remoteVideoTrack);
}

