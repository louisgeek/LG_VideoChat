package com.louisgeek.chat.video;


import android.util.Log;

public class VideoConstants {
    private static final String TAG = "VideoConstants";


    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";
    public static final String VIDEO_CAPTURER_THREAD = "VideoCapturerThread";

    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String VIDEO_CODEC_H264 = "H264";
    public static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
    public static final String VIDEO_CODEC_H264_HIGH = "H264 High";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_ISAC = "ISAC";
    public static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";

    public static final String VIDEO_FLEXFEC_FIELDTRIAL =
            "WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
    public static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
    public static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
            "WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
    public static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
    public static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
    public static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
    public static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
    public static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
    public static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";


    //=====================
//    private static String videoCodec = VIDEO_CODEC_H264_HIGH;
    //    private static String videoCodec = VIDEO_CODEC_VP9;
    private static final String videoCodec = VIDEO_CODEC_H264;

    public static boolean enableH264HighProfile = VIDEO_CODEC_H264_HIGH.contains(videoCodec);
    public static boolean videoFlexfecEnabled = false;
    public static boolean disableWebRtcAGCAndHPF = false;
    public static boolean videoCallEnabled = true;
    public static boolean loopback = false;
    public static boolean tracing = false;
    public static int videoWidth = 1280;
    public static int videoHeight = 720;
    public static int videoFps = 30;
    public static int videoMaxBitrate = 0;
    public static int audioStartBitrate = 0;
    public static String audioCodec = "OPUS";
    public static boolean noAudioProcessing = false;
    public static boolean aecDump = false;
    public static boolean saveInputAudioToFile = false;
    public static boolean useOpenSLES = false;
    public static boolean disableBuiltInAEC = true;
    public static boolean disableBuiltInAGC = true;
    public static boolean disableBuiltInNS = true;
    public static boolean enableRtcEventLog = false;
    public static boolean useLegacyAudioDevice = false;
    public static boolean videoCodecHwAcceleration = true;
    public static boolean enableIntelVp8Encoder = true;
    public static boolean enableAudio = true;
    public static boolean localVideoMirror = false;

    public static String getVideoCodecName() {
        switch (videoCodec) {
            case VIDEO_CODEC_VP8:
                return VIDEO_CODEC_VP8;
            case VIDEO_CODEC_VP9:
                return VIDEO_CODEC_VP9;
            case VIDEO_CODEC_H264_HIGH:
            case VIDEO_CODEC_H264_BASELINE:
                return VIDEO_CODEC_H264;
            default:
                return VIDEO_CODEC_VP8;
        }
    }

    public static String getFieldTrials() {
        String fieldTrials = "";
        if (videoFlexfecEnabled) {
            fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL;
            Log.d(TAG, "Enable FlexFEC field trial.");
        }
        fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
        if (disableWebRtcAGCAndHPF) {
            fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL;
            Log.d(TAG, "Disable WebRTC AGC field trial.");
        }
        return fieldTrials;
    }

}
