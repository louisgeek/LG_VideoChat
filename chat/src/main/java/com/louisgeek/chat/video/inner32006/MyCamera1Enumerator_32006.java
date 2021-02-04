package com.louisgeek.chat.video.inner32006;

import android.hardware.Camera;
import android.os.SystemClock;

import androidx.annotation.Nullable;

import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.Logging;
import org.webrtc.Size;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by louisgeek on 2020/2/28.
 */
public class MyCamera1Enumerator_32006 implements CameraEnumerator {
    private static final String TAG = "MyCameraEnumerator";
    private static List<List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats;
    private final boolean captureToTexture;

    public MyCamera1Enumerator_32006() {
        this(true);
    }

    public MyCamera1Enumerator_32006(boolean captureToTexture) {
        this.captureToTexture = captureToTexture;
    }

    static List<Size> convertSizes(List<Camera.Size> cameraSizes) {
        List<Size> sizes = new ArrayList();
        Iterator var2 = cameraSizes.iterator();

        while (var2.hasNext()) {
            Camera.Size size = (Camera.Size) var2.next();
            sizes.add(new Size(size.width, size.height));
        }

        return sizes;
    }

    static synchronized List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(int cameraId) {
        if (cachedSupportedFormats == null) {
            cachedSupportedFormats = new ArrayList();

            for (int i = 0; i < Camera.getNumberOfCameras(); ++i) {
                cachedSupportedFormats.add(enumerateFormats(i));
            }
        }

        return cachedSupportedFormats.get(cameraId);
    }

    private static List<CameraEnumerationAndroid.CaptureFormat> enumerateFormats(int cameraId) {
        Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + ".");
        long startTimeMs = SystemClock.elapsedRealtime();
        Camera camera = null;

        Camera.Parameters parameters;
        label94:
        {
            ArrayList var6;
            try {
                Logging.d("Camera1Enumerator", "Opening camera with index " + cameraId);
                camera = Camera.open(cameraId);
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
                Camera.Size size = (Camera.Size) var19.next();
                formatList.add(new CameraEnumerationAndroid.CaptureFormat(size.width, size.height, minFps, maxFps));
            }
        } catch (Exception var14) {
            Logging.e("Camera1Enumerator", "getSupportedFormats() failed on camera index " + cameraId, var14);
        }

        long endTimeMs = SystemClock.elapsedRealtime();
        Logging.d("Camera1Enumerator", "Get supported formats for camera index " + cameraId + " done. Time spent: " + (endTimeMs - startTimeMs) + " ms.");
        return formatList;
    }

    static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(List<int[]> arrayRanges) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> ranges = new ArrayList();
        Iterator var2 = arrayRanges.iterator();

        while (var2.hasNext()) {
            int[] range = (int[]) var2.next();
            ranges.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(range[0], range[1]));
        }

        return ranges;
    }

    @Nullable
    private static Camera.CameraInfo getCameraInfo(int index) {
        Camera.CameraInfo info = new Camera.CameraInfo();

        try {
            Camera.getCameraInfo(index, info);
            return info;
        } catch (Exception var3) {
            Logging.e("Camera1Enumerator", "getCameraInfo failed on index " + index, var3);
            return null;
        }
    }

    @Nullable
    static String getDeviceName(int index) {
        Camera.CameraInfo info = getCameraInfo(index);
        if (info == null) {
            return null;
        } else {
            String facing = info.facing == 1 ? "front" : "back";
            return "Camera " + index + ", Facing " + facing + ", Orientation " + info.orientation;
        }
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

    public boolean isCaptureToTexture() {
        return captureToTexture;
    }

    @Override
    public boolean isFrontFacing(String deviceName) {
        Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 1;
    }

    @Override
    public boolean isBackFacing(String deviceName) {
        Camera.CameraInfo info = getCameraInfo(getCameraIndex(deviceName));
        return info != null && info.facing == 0;
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
    public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(String deviceName) {
        return getSupportedFormats(getCameraIndex(deviceName));
    }

    @Override
    public CameraVideoCapturer createCapturer(String deviceName, CameraVideoCapturer.CameraEventsHandler cameraEventsHandler) {
        return new MyCameraCapturer_32006(deviceName, cameraEventsHandler, new MyCamera1Enumerator_32006(captureToTexture));
    }
}
