package com.louisgeek.chat.video.video;

import android.content.Context;

import com.louisgeek.chat.video.VideoConstants;
import com.louisgeek.chat.video.video.inner.MyCameraEnumerator;

import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.VideoCapturer;

/**
 * Created by louisgeek on 2019/10/22.
 */
public class MyCameraVideoCapturerHelper {
    private static final String TAG = "CameraVideoCapturerHelp";


    public static VideoCapturer getBackFacingCameraVideoCapturer(Context context) {
        CameraEnumerator cameraEnumerator = new MyCameraEnumerator(true);
        //找后置
        for (String deviceName : cameraEnumerator.getDeviceNames()) {
            if (cameraEnumerator.isBackFacing(deviceName)) {
                CameraVideoCapturer cameraVideoCapturer = cameraEnumerator.createCapturer(deviceName, null);
                if (cameraVideoCapturer != null) {
                    return cameraVideoCapturer;
                }
            }
        }
        return null;
    }

    public static VideoCapturer getFrontFacingCameraVideoCapturer(Context context) {
        CameraEnumerator cameraEnumerator = new MyCameraEnumerator(true);
        //找前置
        for (String deviceName : cameraEnumerator.getDeviceNames()) {
            if (cameraEnumerator.isFrontFacing(deviceName)) {
                CameraVideoCapturer cameraVideoCapturer = cameraEnumerator.createCapturer(deviceName, null);
                if (cameraVideoCapturer != null) {
                    return cameraVideoCapturer;
                }
            }
        }
        return null;
    }


    //开始摄像头预览
    public static void startCameraVideoCapturer(VideoCapturer videoCapturer) {
        if (videoCapturer != null) {
            videoCapturer.startCapture(VideoConstants.videoWidth, VideoConstants.videoHeight, VideoConstants.videoFps);
        }
    }


    //停止摄像头预览
    public static void stopCameraVideoCapturer(VideoCapturer videoCapturer) {
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //切换摄像头
    public static void switchCameraVideoCapturer(CameraVideoCapturer cameraVideoCapturer, CameraVideoCapturer.CameraSwitchHandler cameraSwitchHandler) {
        if (cameraVideoCapturer != null) {
            cameraVideoCapturer.switchCamera(cameraSwitchHandler);
        }
    }
}
