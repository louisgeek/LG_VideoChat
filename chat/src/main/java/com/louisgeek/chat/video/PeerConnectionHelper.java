package com.louisgeek.chat.video;

import android.content.Context;
import android.util.Log;

import org.webrtc.PeerConnection;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.ArrayList;
import java.util.List;

public class PeerConnectionHelper {
    private static final String TAG = "PeerConnectionHelper";


    /* //创建音频模式LegacyAudioDevice
     private AudioDeviceModule createLegacyAudioDevice() {
         // Enable/disable OpenSL ES playback.
         if (!VideoConstants.useOpenSLES) {
             Log.d(TAG, "Disable OpenSL ES audio even if device supports it");
             WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(true *//* enable *//*);
        } else {
            Log.d(TAG, "Allow OpenSL ES audio if device supports it");
            WebRtcAudioManager.setBlacklistDeviceForOpenSLESUsage(false);
        }
        if (VideoConstants.disableBuiltInAEC) {
            Log.d(TAG, "Disable built-in AEC even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true);
        } else {
            Log.d(TAG, "Enable built-in AEC if device supports it");
            WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(false);
        }

        if (VideoConstants.disableBuiltInNS) {
            Log.d(TAG, "Disable built-in NS even if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true);
        } else {
            Log.d(TAG, "Enable built-in NS if device supports it");
            WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(false);
        }

        //WebRtcAudioRecord.setOnAudioSamplesReady(saveRecordedAudioToFile);

        // Set audio record error callbacks.
        WebRtcAudioRecord.setErrorCallback(new WebRtcAudioRecord.WebRtcAudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordStartError(
                    WebRtcAudioRecord.AudioRecordStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordError: " + errorMessage);
            }
        });

        WebRtcAudioTrack.setErrorCallback(new WebRtcAudioTrack.ErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackStartError(
                    WebRtcAudioTrack.AudioTrackStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackError: " + errorMessage);
            }
        });
//        return legacy
        return new LegacyAudioDeviceModule();
    }
*/
    //创建音频模式JavaAudioDevice
    public static AudioDeviceModule createJavaAudioDevice(Context context) {
        // Enable/disable OpenSL ES playback.
        if (!VideoConstants.useOpenSLES) {
            Log.w(TAG, "External OpenSLES ADM not implemented yet.");
        }

        // Set audio record error callbacks.
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = new JavaAudioDeviceModule.AudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordStartError(
                    JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordError: " + errorMessage);
            }
        };

        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = new JavaAudioDeviceModule.AudioTrackErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackInitError: " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackStartError(
                    JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackStartError: " + errorCode + ". " + errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackError: " + errorMessage);
            }
        };

        return JavaAudioDeviceModule.builder(context.getApplicationContext())
                //.setSamplesReadyCallback(saveRecordedAudioToFile)
                .setUseHardwareAcousticEchoCanceler(!VideoConstants.disableBuiltInAEC)
                .setUseHardwareNoiseSuppressor(!VideoConstants.disableBuiltInNS)
                .setAudioRecordErrorCallback(audioRecordErrorCallback)
                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .createAudioDeviceModule();
    }


    public static PeerConnection.RTCConfiguration getRTCConfiguration() {
        //
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.xten.com").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.xten.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voipbuster.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voxgratia.org:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.sipgate.net:10000").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.ekiga.net:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.ideasip.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.schlund.de:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voiparound.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voipbuster.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voipstunt.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:numb.viagenie.ca:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.counterpath.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.1und1.de:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.gmx.net:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.bcs2005.net:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.callwithus.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.counterpath.net:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.internetcalls.com:3478").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.voip.aebc.com:3478").createIceServer());
        //
        PeerConnection.RTCConfiguration rtcConfiguration =
                new PeerConnection.RTCConfiguration(iceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfiguration.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfiguration.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfiguration.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfiguration.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        // Use ECDSA encryption.
        rtcConfiguration.keyType = PeerConnection.KeyType.ECDSA;
        // Enable DTLS for normal calls and disable for loopback calls.
        rtcConfiguration.enableDtlsSrtp = !VideoConstants.loopback;
        rtcConfiguration.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
        return rtcConfiguration;
    }

}
