package com.louisgeek.chat.video.inner;

import android.content.Context;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;

import org.webrtc.CameraEnumerationAndroid;
import org.webrtc.Logging;
import org.webrtc.NV21Buffer;
import org.webrtc.Size;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.TextureBufferImpl;
import org.webrtc.VideoFrame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by louisgeek on 2020/2/28.
 */
public class MyCamera1Session {

    private static final String TAG = "MyCamera1Session";
    /* int shine_orientation = 270;//组合 270 false
     boolean shine_mirror = false;*/
    int shine_orientation = 90;//组合 90 true
    boolean shine_mirror = true;
    int dnake_orientation = 0;//组合 0 false
    boolean danke_mirror = false;
    private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
    /*   private static final Histogram camera1StartTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StartTimeMs", 1, 10000, 50);
       private static final Histogram camera1StopTimeMsHistogram = Histogram.createCounts("WebRTC.Android.Camera1.StopTimeMs", 1, 10000, 50);
       private static final Histogram camera1ResolutionHistogram;*/
    private final Handler cameraThreadHandler;
    private final Events events;
    private final boolean captureToTexture;
    private final Context applicationContext;
    private final SurfaceTextureHelper surfaceTextureHelper;
    private final int cameraId;
    private final Camera camera;
    private final Camera.CameraInfo info;
    private final CameraEnumerationAndroid.CaptureFormat captureFormat;
    private final long constructionTimeNs;
    private SessionState state;
    private boolean firstFrameReported;

    static int getDeviceOrientation(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        switch (wm.getDefaultDisplay().getRotation()) {
            case 0:
            default:
                return 0;
            case 1:
                return 90;
            case 2:
                return 180;
            case 3:
                return 270;
        }
    }

    static VideoFrame.TextureBuffer createTextureBufferWithModifiedTransformMatrix(TextureBufferImpl buffer, boolean mirror, int rotation) {
        Matrix transformMatrix = new Matrix();
        transformMatrix.preTranslate(0.5F, 0.5F);
        if (mirror) {
            transformMatrix.preScale(-1.0F, 1.0F);
        }

        transformMatrix.preRotate((float) rotation);
        transformMatrix.preTranslate(-0.5F, -0.5F);
        return buffer.applyTransformMatrix(transformMatrix, buffer.getWidth(), buffer.getHeight());
    }

    public interface Events {
        void onCameraOpening();

        void onCameraError(MyCamera1Session var1, String var2);

        void onCameraDisconnected(MyCamera1Session var1);

        void onCameraClosed(MyCamera1Session var1);

        void onFrameCaptured(MyCamera1Session var1, VideoFrame var2);
    }

    public interface CreateSessionCallback {
        void onDone(MyCamera1Session var1);

        void onFailure(FailureType var1, String var2);
    }

    public enum FailureType {
        ERROR,
        DISCONNECTED;

        FailureType() {
        }
    }

    public static void create(CreateSessionCallback callback, Events events, boolean captureToTexture, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, int cameraId, int width, int height, int framerate) {
        long constructionTimeNs = System.nanoTime();
        Logging.d(TAG, "Open camera " + cameraId);
        events.onCameraOpening();

        Camera camera;
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException var19) {
            callback.onFailure(FailureType.ERROR, var19.getMessage());
            return;
        }

        if (camera == null) {
            callback.onFailure(FailureType.ERROR, "android.hardware.Camera.open returned null for camera id = " + cameraId);
        } else {
            try {
                camera.setPreviewTexture(surfaceTextureHelper.getSurfaceTexture());
            } catch (RuntimeException | IOException var18) {
                camera.release();
                callback.onFailure(FailureType.ERROR, var18.getMessage());
                return;
            }

            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);

            CameraEnumerationAndroid.CaptureFormat captureFormat;
            try {
                Camera.Parameters parameters = camera.getParameters();
                captureFormat = findClosestCaptureFormat(parameters, width, height, framerate);
                Size pictureSize = findClosestPictureSize(parameters, width, height);
                updateCameraParameters(camera, parameters, captureFormat, pictureSize, captureToTexture);
            } catch (RuntimeException var17) {
                camera.release();
                callback.onFailure(FailureType.ERROR, var17.getMessage());
                return;
            }

            if (!captureToTexture) {
                int frameSize = captureFormat.frameSize();

                for (int i = 0; i < 3; ++i) {
                    ByteBuffer buffer = ByteBuffer.allocateDirect(frameSize);
                    camera.addCallbackBuffer(buffer.array());
                }
            }

            camera.setDisplayOrientation(0);
            callback.onDone(new MyCamera1Session(events, captureToTexture, applicationContext, surfaceTextureHelper, cameraId, camera, info, captureFormat, constructionTimeNs));
        }
    }

    private static void updateCameraParameters(Camera camera, Camera.Parameters parameters, CameraEnumerationAndroid.CaptureFormat captureFormat, Size pictureSize, boolean captureToTexture) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        parameters.setPreviewFpsRange(captureFormat.framerate.min, captureFormat.framerate.max);
        parameters.setPreviewSize(captureFormat.width, captureFormat.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        if (!captureToTexture) {
            Objects.requireNonNull(captureFormat);
            parameters.setPreviewFormat(17);
        }

        if (parameters.isVideoStabilizationSupported()) {
            parameters.setVideoStabilization(true);
        }

        if (focusModes.contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }
        camera.setParameters(parameters);
    }

    private static CameraEnumerationAndroid.CaptureFormat findClosestCaptureFormat(Camera.Parameters parameters, int width, int height, int framerate) {
        List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> supportedFramerates = MyCameraEnumerator.convertFramerates(parameters.getSupportedPreviewFpsRange());
        Logging.d(TAG, "Available fps ranges: " + supportedFramerates);
        CameraEnumerationAndroid.CaptureFormat.FramerateRange fpsRange = CameraEnumerationAndroid.getClosestSupportedFramerateRange(supportedFramerates, framerate);
        Size previewSize = CameraEnumerationAndroid.getClosestSupportedSize(MyCameraEnumerator.convertSizes(parameters.getSupportedPreviewSizes()), width, height);
//        reportCameraResolution(camera1ResolutionHistogram, previewSize);
        return new CameraEnumerationAndroid.CaptureFormat(previewSize.width, previewSize.height, fpsRange);
    }

    static final ArrayList<Size> COMMON_RESOLUTIONS = new ArrayList(Arrays.asList(new Size(160, 120), new Size(240, 160), new Size(320, 240), new Size(400, 240), new Size(480, 320), new Size(640, 360), new Size(640, 480), new Size(768, 480), new Size(854, 480), new Size(800, 600), new Size(960, 540), new Size(960, 640), new Size(1024, 576), new Size(1024, 600), new Size(1280, 720), new Size(1280, 1024), new Size(1920, 1080), new Size(1920, 1440), new Size(2560, 1440), new Size(3840, 2160)));

   /* static void reportCameraResolution(Histogram histogram, Size resolution) {
        int index = COMMON_RESOLUTIONS.indexOf(resolution);
        histogram.addSample(index + 1);
    }*/

    private static Size findClosestPictureSize(Camera.Parameters parameters, int width, int height) {
        return CameraEnumerationAndroid.getClosestSupportedSize(MyCameraEnumerator.convertSizes(parameters.getSupportedPictureSizes()), width, height);
    }

    private MyCamera1Session(Events events, boolean captureToTexture, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, int cameraId, Camera camera, Camera.CameraInfo info, CameraEnumerationAndroid.CaptureFormat captureFormat, long constructionTimeNs) {
        Logging.d(TAG, "Create new camera1 session on camera " + cameraId);
        this.cameraThreadHandler = new Handler();
        this.events = events;
        this.captureToTexture = captureToTexture;
        this.applicationContext = applicationContext;
        this.surfaceTextureHelper = surfaceTextureHelper;
        this.cameraId = cameraId;
        this.camera = camera;
        this.info = info;
        this.captureFormat = captureFormat;
        this.constructionTimeNs = constructionTimeNs;
        surfaceTextureHelper.setTextureSize(captureFormat.width, captureFormat.height);
        this.startCapturing();
    }

    public void stop() {
        Logging.d(TAG, "Stop camera1 session on camera " + this.cameraId);
        this.checkIsOnCameraThread();
        if (this.state != SessionState.STOPPED) {
            long stopStartTime = System.nanoTime();
            this.stopInternal();
            int stopTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - stopStartTime);
//            camera1StopTimeMsHistogram.addSample(stopTimeMs);
        }

    }


    private void startCapturing() {
        Logging.d(TAG, "Start capturing");
        this.checkIsOnCameraThread();
        this.state = SessionState.RUNNING;
        this.camera.setErrorCallback(new Camera.ErrorCallback() {
            @Override
            public void onError(int error, Camera camera) {
                String errorMessage;
                if (error == 100) {
                    errorMessage = "Camera server died!";
                } else {
                    errorMessage = "Camera error: " + error;
                }

                Logging.e(TAG, errorMessage);
                stopInternal();
                if (error == 2) {
                    events.onCameraDisconnected(MyCamera1Session.this);
                } else {
                    events.onCameraError(MyCamera1Session.this, errorMessage);
                }

            }
        });
        if (this.captureToTexture) {
            Log.e(TAG, "listenForTextureFrames: ");
            boolean mirror = getMirror(this.info.facing);
            this.listenForTextureFrames(mirror);
        } else {
            this.listenForBytebufferFrames();
        }

        try {
            this.camera.startPreview();
        } catch (RuntimeException var2) {
            this.stopInternal();
            this.events.onCameraError(this, var2.getMessage());
        }

    }
    private   int getFrameOrientation() {
        if ("Allwinner".equals(Build.MANUFACTURER) && "QUAD-CORE A64 p1".equals(Build.MODEL)) {
            return shine_orientation;
        }else if ("softwinner".equals(Build.MANUFACTURER) && "v902".equals(Build.MODEL)){
            return dnake_orientation;
        }else {
            return getDefaultFrameOrientation();
        }
    }
    private   boolean getMirror(int facing) {
        if ("Allwinner".equals(Build.MANUFACTURER) && "QUAD-CORE A64 p1".equals(Build.MODEL)) {
            return shine_mirror;
        }else if ("softwinner".equals(Build.MANUFACTURER) && "v902".equals(Build.MODEL)){
            return danke_mirror;
        }else {
            return facing == 1;
        }
    }
    private void stopInternal() {
        Logging.d(TAG, "Stop internal");
        this.checkIsOnCameraThread();
        if (this.state == SessionState.STOPPED) {
            Logging.d(TAG, "Camera is already stopped");
        } else {
            this.state = SessionState.STOPPED;
            this.surfaceTextureHelper.stopListening();
            this.camera.stopPreview();
            this.camera.release();
            this.events.onCameraClosed(this);
            Logging.d(TAG, "Stop done");
        }
    }

    private void listenForTextureFrames(boolean mirror) {
        this.surfaceTextureHelper.startListening((frame) -> {
            this.checkIsOnCameraThread();
            if (this.state != SessionState.RUNNING) {
                Logging.d(TAG, "Texture frame captured but camera is no longer running.");
            } else {
                if (!this.firstFrameReported) {
                    int startTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.constructionTimeNs);
//                    camera1StartTimeMsHistogram.addSample(startTimeMs);
                    this.firstFrameReported = true;
                }
                Log.d(TAG, "PP====dnk listenForTextureFrames: "+this.getFrameOrientation());
                VideoFrame modifiedFrame = new VideoFrame(createTextureBufferWithModifiedTransformMatrix((TextureBufferImpl) frame.getBuffer(), mirror, 0), this.getFrameOrientation(), frame.getTimestampNs());
                this.events.onFrameCaptured(this, modifiedFrame);
                modifiedFrame.release();
            }
        });
    }

    private void listenForBytebufferFrames() {
        this.camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera callbackCamera) {
                checkIsOnCameraThread();
                if (callbackCamera != camera) {
                    Logging.e(TAG, "Callback from a different camera. This should never happen.");
                } else if (state != SessionState.RUNNING) {
                    Logging.d(TAG, "Bytebuffer frame captured but camera is no longer running.");
                } else {
                    long captureTimeNs = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
                    if (!firstFrameReported) {
                        int startTimeMs = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - constructionTimeNs);
//                        camera1StartTimeMsHistogram.addSample(startTimeMs);
                        firstFrameReported = true;
                    }

                    VideoFrame.Buffer frameBuffer = new NV21Buffer(data, captureFormat.width, captureFormat.height, () -> {
                        cameraThreadHandler.post(() -> {
                            if (state == SessionState.RUNNING) {
                                camera.addCallbackBuffer(data);
                            }

                        });
                    });
                    Log.d(TAG, "PP====dnk onPreviewFrame: "+getFrameOrientation());
                    VideoFrame frame = new VideoFrame(frameBuffer, getFrameOrientation(), captureTimeNs);
                    events.onFrameCaptured(MyCamera1Session.this, frame);
                    frame.release();
                }
            }
        });
    }

    private int getDefaultFrameOrientation() {
        int rotation = getDeviceOrientation(this.applicationContext);
        int frameRotation;
        if (this.info.facing == 0) {
            //横屏的时候 使用后置摄像头
            // 手机   deviceOrientation 90
            // 床头卡 deviceOrientation 0
            frameRotation = 360 - rotation;
        } else {
            //横屏的时候 使用前置摄像头
            // 手机 orientation 270 + deviceOrientation 90
            // 床头卡 orientation 0 + deviceOrientation 0
            frameRotation = (info.orientation + rotation) % 360;
        }
        return frameRotation;
    }

    @Deprecated
    private int getFrameOrientationOld() {
        int rotation = getDeviceOrientation(this.applicationContext);
        if (this.info.facing == 0) {
            rotation = 360 - rotation;
        }

        return (this.info.orientation + rotation) % 360;
    }

    private void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            throw new IllegalStateException("Wrong thread");
        }
    }

    static {
//        camera1ResolutionHistogram = Histogram.createEnumeration("WebRTC.Android.Camera1.Resolution", CameraEnumerationAndroid.COMMON_RESOLUTIONS.size());
    }

    private enum SessionState {
        RUNNING,
        STOPPED;

        SessionState() {
        }
    }
}
