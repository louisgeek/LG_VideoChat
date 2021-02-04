package com.louisgeek.chat.video;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;

import org.webrtc.Camera1Capturer;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat;
import org.webrtc.CameraEnumerationAndroid.CaptureFormat.FramerateRange;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.Logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class Camera1Enumerator2 implements CameraEnumerator {
    private static final String TAG = "Camera1Enumerator2";
    private static List<List<CaptureFormat>> cachedSupportedFormats;
    private final boolean captureToTexture;
    private static int mDefaultDisplayRotation;

    public Camera1Enumerator2() {
        this(true, 0);
    }

    public Camera1Enumerator2(boolean captureToTexture, int defaultDisplayRotation) {
        this.captureToTexture = captureToTexture;
        mDefaultDisplayRotation = defaultDisplayRotation;
    }

    private static int getRealDegrees(int cameraId, int rotation) {
        Log.e(TAG, "getRealDegrees: " + rotation);
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
        }

        int result;
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            Log.e(TAG, "getRealDegrees: CAMERA_FACING_FRONT " + info.orientation);
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            Log.e(TAG, "getRealDegrees: CAMERA_FACING_BACK " + info.orientation);
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    static synchronized List<CaptureFormat> getSupportedFormats(int cameraId) {
        if (cachedSupportedFormats == null) {
            cachedSupportedFormats = new ArrayList();

            for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
                cachedSupportedFormats.add(enumerateFormats(i));
            }
        }

        return cachedSupportedFormats.get(cameraId);
    }

    @Override
    public boolean isFrontFacing(String deviceName) {
        CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 1;
    }

    @Override
    public boolean isBackFacing(String deviceName) {
        CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 0;
    }

    @Override
    public List<CaptureFormat> getSupportedFormats(String deviceName) {
        return getSupportedFormats(getCameraIndex(deviceName));
    }

    @Nullable
    private static CameraInfo getCameraInfo(int index) {
        CameraInfo info = new CameraInfo();

        try {
            Camera.getCameraInfo(index, info);
            return info;
        } catch (Exception var3) {
            Logging.e("Camera1Enumerator", "getCameraInfo failed on index " + index, var3);
            return null;
        }
    }

    @Override
    public String[] getDeviceNames() {
        ArrayList<String> namesList = new ArrayList();

        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            String name = getDeviceName(i);
            if (name != null) {
                namesList.add(name);
                Logging.d("Camera1Enumerator", "Index: " + i + ". " + name);
            } else {
                Logging.e("Camera1Enumerator", "Index: " + i + ". Failed to query camera name.");
            }
        }

        String[] namesArray = new String[namesList.size()];
        return namesList.toArray(namesArray);
    }

    @Override
    public CameraVideoCapturer createCapturer(String deviceName, CameraEventsHandler eventsHandler) {
        return new Camera1Capturer(deviceName, eventsHandler, this.captureToTexture);
    }

    private static List<CaptureFormat> enumerateFormats(int cameraId) {
        Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + ".");
        long startTimeMs = SystemClock.elapsedRealtime();
        Camera camera = null;

        Parameters parameters;
        label94:
        {
            ArrayList var6;
            try {
                Logging.d("Camera1Enumerator", "Opening camera with index " + cameraId);
                camera = Camera.open(cameraId);
                camera.setDisplayOrientation(getRealDegrees(cameraId, mDefaultDisplayRotation));
                parameters = camera.getParameters();
                break label94;
            } catch (RuntimeException var15) {
                Logging.e("Camera1Enumerator", "Open camera failed on camera index " + cameraId, var15);
                var6 = new ArrayList();
            } finally {
                if (camera != null) {
                    camera.release();
                }

            }

            return var6;
        }

        ArrayList formatList = new ArrayList();

        try {
            int minFps = 0;
            int maxFps = 0;
            List<int[]> listFpsRange = parameters.getSupportedPreviewFpsRange();
            if (listFpsRange != null) {
                int[] range = listFpsRange.get(listFpsRange.size() - 1);
                minFps = range[0];
                maxFps = range[1];
            }

            Iterator var19 = parameters.getSupportedPreviewSizes().iterator();

            while (var19.hasNext()) {
                Size size = (Size) var19.next();
                formatList.add(new CaptureFormat(size.width, size.height, minFps, maxFps));
            }
        } catch (Exception var14) {
            Logging.e("Camera1Enumerator", "getSupportedFormats() failed on camera index " + cameraId, var14);
        }

        long endTimeMs = SystemClock.elapsedRealtime();
        Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + " done. Time spent: " + (endTimeMs - startTimeMs) + " ms.");
        return formatList;
    }

    static List<org.webrtc.Size> convertSizes(List<Size> cameraSizes) {
        List<org.webrtc.Size> sizes = new ArrayList();
        Iterator var2 = cameraSizes.iterator();

        while (var2.hasNext()) {
            Size size = (Size) var2.next();
            sizes.add(new org.webrtc.Size(size.width, size.height));
        }

        return sizes;
    }

    static List<FramerateRange> convertFramerates(List<int[]> arrayRanges) {
        List<FramerateRange> ranges = new ArrayList();
        Iterator var2 = arrayRanges.iterator();

        while (var2.hasNext()) {
            int[] range = (int[]) var2.next();
            ranges.add(new FramerateRange(range[0], range[1]));
        }

        return ranges;
    }

    static int getCameraIndex(String deviceName) {
        Logging.d("Camera1Enumerator", "getCameraIndex: " + deviceName);

        for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            if (deviceName.equals(getDeviceName(i))) {
                return i;
            }
        }

        throw new IllegalArgumentException("No such camera: " + deviceName);
    }

    @Nullable
    static String getDeviceName(int index) {
        CameraInfo info = getCameraInfo(index);
        if (info == null) {
            return null;
        } else {
            String facing = info.facing == 1 ? "front" : "back";
            return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
        }
    }
}
