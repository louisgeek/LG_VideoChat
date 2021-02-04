package com.louisgeek.chat.video.inner32006;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.CapturerObserver;
import org.webrtc.Logging;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoFrame;

import java.util.Arrays;
import java.util.List;

/**
 * Created by louisgeek on 2020/11/10.
 */
public class MyCameraCapturer_32006 implements CameraVideoCapturer {
    private static final String TAG = "MyCameraCapturer_32006";
    private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
    private static final int OPEN_CAMERA_DELAY_MS = 500;
    private static final int OPEN_CAMERA_TIMEOUT = 10000;
    private final CameraEnumerator cameraEnumerator;
    @Nullable
    private final CameraEventsHandler eventsHandler;
    private final Handler uiThreadHandler;

    private final boolean captureToTexture;

    private final Runnable openCameraTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            eventsHandler.onCameraError("Camera failed to start within timeout.");
        }
    };
    private final Object stateLock = new Object();
    private Handler cameraThreadHandler;
    private Context applicationContext;
    private CapturerObserver capturerObserver;
    private SurfaceTextureHelper surfaceHelper;
    private boolean sessionOpening;
    @Nullable
    private MyCamera1Session_32006 currentSession;
    private String cameraName;
    private String pendingCameraName;
    private int width;
    private int height;
    private int framerate;
    private int openAttemptsRemaining;
    private SwitchState switchState;
    @Nullable
    private CameraSwitchHandler switchEventsHandler;
    @Nullable
    private CameraStatistics cameraStatistics;
    private boolean firstFrameObserved;
    @Nullable
    private final MyCamera1Session_32006.Events cameraSessionEventsHandler = new MyCamera1Session_32006.Events() {
        @Override
        public void onCameraOpening() {
            checkIsOnCameraThread();
            synchronized (stateLock) {
                if (currentSession != null) {
                    Logging.w(TAG, "onCameraOpening while session was open.");
                } else {
                    eventsHandler.onCameraOpening(cameraName);
                }
            }
        }

        @Override
        public void onCameraError(MyCamera1Session_32006 session, String error) {
            checkIsOnCameraThread();
            synchronized (stateLock) {
                if (session != currentSession) {
                    Logging.w(TAG, "onCameraError from another session: " + error);
                } else {
                    eventsHandler.onCameraError(error);
                    stopCapture();
                }
            }
        }

        @Override
        public void onCameraDisconnected(MyCamera1Session_32006 session) {
            checkIsOnCameraThread();
            synchronized (stateLock) {
                if (session != currentSession) {
                    Logging.w(TAG, "onCameraDisconnected from another session.");
                } else {
                    eventsHandler.onCameraDisconnected();
                    stopCapture();
                }
            }
        }

        @Override
        public void onCameraClosed(MyCamera1Session_32006 session) {
            checkIsOnCameraThread();
            synchronized (stateLock) {
                if (session != currentSession && currentSession != null) {
                    Logging.d(TAG, "onCameraClosed from another session.");
                } else {
                    eventsHandler.onCameraClosed();
                }
            }
        }

        @Override
        public void onFrameCaptured(MyCamera1Session_32006 session, VideoFrame frame) {
            checkIsOnCameraThread();
            synchronized (stateLock) {
                if (session != currentSession) {
                    Logging.w(TAG, "onFrameCaptured from another session.");
                } else {
                    if (!firstFrameObserved) {
                        eventsHandler.onFirstFrameAvailable();
                        firstFrameObserved = true;
                    }

                    cameraStatistics.addFrame();
                    capturerObserver.onFrameCaptured(frame);
                }
            }
        }
    };
    @Nullable
    private final MyCamera1Session_32006.CreateSessionCallback createSessionCallback = new MyCamera1Session_32006.CreateSessionCallback() {
        @Override
        public void onDone(MyCamera1Session_32006 session) {
            checkIsOnCameraThread();
            Logging.d(TAG, "Create session done. Switch state: " + switchState);
            uiThreadHandler.removeCallbacks(openCameraTimeoutRunnable);
            synchronized (stateLock) {
                capturerObserver.onCapturerStarted(true);
                sessionOpening = false;
                currentSession = session;
                cameraStatistics = new CameraStatistics(surfaceHelper, eventsHandler);
                firstFrameObserved = false;
                stateLock.notifyAll();
                if (switchState == SwitchState.IN_PROGRESS) {
                    switchState = SwitchState.IDLE;
                    if (switchEventsHandler != null) {
                        switchEventsHandler.onCameraSwitchDone(cameraEnumerator.isFrontFacing(cameraName));
                        switchEventsHandler = null;
                    }
                } else if (switchState == SwitchState.PENDING) {
                    String selectedCameraName = pendingCameraName;
                    pendingCameraName = null;
                    switchState = SwitchState.IDLE;
                    switchCameraInternal(switchEventsHandler, selectedCameraName);
                }

            }
        }

        @Override
        public void onFailure(MyCamera1Session_32006.FailureType failureType, String error) {
            checkIsOnCameraThread();
            uiThreadHandler.removeCallbacks(openCameraTimeoutRunnable);
            synchronized (stateLock) {
                capturerObserver.onCapturerStarted(false);
                openAttemptsRemaining--;
                if (openAttemptsRemaining <= 0) {
                    Logging.w(TAG, "Opening camera failed, passing: " + error);
                    sessionOpening = false;
                    stateLock.notifyAll();
                    if (switchState != SwitchState.IDLE) {
                        if (switchEventsHandler != null) {
                            switchEventsHandler.onCameraSwitchError(error);
                            switchEventsHandler = null;
                        }

                        switchState = SwitchState.IDLE;
                    }

                    if (failureType == MyCamera1Session_32006.FailureType.DISCONNECTED) {
                        eventsHandler.onCameraDisconnected();
                    } else {
                        eventsHandler.onCameraError(error);
                    }
                } else {
                    Logging.w(TAG, "Opening camera failed, retry: " + error);
                    createSessionInternal(500);
                }

            }
        }
    };

    public MyCameraCapturer_32006(String cameraName, @Nullable CameraEventsHandler eventsHandler, MyCamera1Enumerator_32006 cameraEnumerator) {
        this.captureToTexture = cameraEnumerator.isCaptureToTexture();
        this.switchState = SwitchState.IDLE;
        if (eventsHandler == null) {
            eventsHandler = new CameraEventsHandler() {
                @Override
                public void onCameraError(String errorDescription) {
                }

                @Override
                public void onCameraDisconnected() {
                }

                @Override
                public void onCameraFreezed(String errorDescription) {
                }

                @Override
                public void onCameraOpening(String cameraName) {
                }

                @Override
                public void onFirstFrameAvailable() {
                }

                @Override
                public void onCameraClosed() {
                }
            };
        }

        this.eventsHandler = eventsHandler;
        this.cameraEnumerator = cameraEnumerator;
        this.cameraName = cameraName;
        this.uiThreadHandler = new Handler(Looper.getMainLooper());
        List<String> deviceNames = Arrays.asList(cameraEnumerator.getDeviceNames());
        if (deviceNames.isEmpty()) {
            throw new RuntimeException("No cameras attached.");
        } else if (!deviceNames.contains(this.cameraName)) {
            throw new IllegalArgumentException("Camera name " + this.cameraName + " does not match any known camera device.");
        }
    }

    protected void createCameraSession(MyCamera1Session_32006.CreateSessionCallback createSessionCallback, MyCamera1Session_32006.Events events, Context applicationContext, SurfaceTextureHelper surfaceTextureHelper, String cameraName, int width, int height, int framerate) {
        MyCamera1Session_32006.create(createSessionCallback, events, this.captureToTexture, applicationContext, surfaceTextureHelper, MyCamera1Enumerator_32006.getCameraIndex(cameraName), width, height, framerate);
    }

    @Override
    public void initialize(SurfaceTextureHelper surfaceTextureHelper, Context applicationContext, CapturerObserver capturerObserver) {
        this.applicationContext = applicationContext;
        this.capturerObserver = capturerObserver;
        this.surfaceHelper = surfaceTextureHelper;
        this.cameraThreadHandler = surfaceTextureHelper.getHandler();
    }

    @Override
    public void startCapture(int width, int height, int framerate) {
        Logging.d(TAG, "startCapture: " + width + "x" + height + "@" + framerate);
        if (this.applicationContext == null) {
            throw new RuntimeException("CameraCapturer must be initialized before calling startCapture.");
        } else {
            synchronized (this.stateLock) {
                if (!this.sessionOpening && this.currentSession == null) {
                    this.width = width;
                    this.height = height;
                    this.framerate = framerate;
                    this.sessionOpening = true;
                    this.openAttemptsRemaining = 3;
                    this.createSessionInternal(0);
                } else {
                    Logging.w(TAG, "Session already open");
                }
            }
        }
    }

    private void createSessionInternal(int delayMs) {
        this.uiThreadHandler.postDelayed(this.openCameraTimeoutRunnable, delayMs + 10000);
        this.cameraThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //
                createCameraSession(createSessionCallback, cameraSessionEventsHandler, applicationContext, surfaceHelper, cameraName, width, height, framerate);
            }
        }, delayMs);
    }

    @Override
    public void stopCapture() {
        Logging.d(TAG, "Stop capture");
        synchronized (this.stateLock) {
            while (this.sessionOpening) {
                Logging.d(TAG, "Stop capture: Waiting for session to open");

                try {
                    this.stateLock.wait();
                } catch (InterruptedException var4) {
                    Logging.w(TAG, "Stop capture interrupted while waiting for the session to open.");
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            if (this.currentSession != null) {
                Logging.d(TAG, "Stop capture: Nulling session");
                this.cameraStatistics.release();
                this.cameraStatistics = null;
                final MyCamera1Session_32006 oldSession = this.currentSession;
                this.cameraThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        oldSession.stop();
                    }
                });
                this.currentSession = null;
                this.capturerObserver.onCapturerStopped();
            } else {
                Logging.d(TAG, "Stop capture: No session open");
            }
        }

        Logging.d(TAG, "Stop capture done");
    }

    @Override
    public void changeCaptureFormat(int width, int height, int framerate) {
        Logging.d(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate);
        synchronized (this.stateLock) {
            this.stopCapture();
            this.startCapture(width, height, framerate);
        }
    }

    @Override
    public void dispose() {
        Logging.d(TAG, "dispose");
        this.stopCapture();
    }

    @Override
    public void switchCamera(final CameraSwitchHandler switchEventsHandler) {
        Logging.d(TAG, "switchCamera");
        this.cameraThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                List<String> deviceNames = Arrays.asList(cameraEnumerator.getDeviceNames());
                if (deviceNames.size() < 2) {
                    reportCameraSwitchError("No camera to switch to.", switchEventsHandler);
                } else {
                    int cameraNameIndex = deviceNames.indexOf(cameraName);
                    String cameraName = deviceNames.get((cameraNameIndex + 1) % deviceNames.size());
                    switchCameraInternal(switchEventsHandler, cameraName);
                }
            }
        });
    }

    public void switchCamera(final CameraSwitchHandler switchEventsHandler, final String cameraName) {
        Logging.d(TAG, "switchCamera");
        this.cameraThreadHandler.post(new Runnable() {
            public void run() {
                switchCameraInternal(switchEventsHandler, cameraName);
            }
        });
    }

    @Override
    public boolean isScreencast() {
        return false;
    }

    public void printStackTrace() {
        Thread cameraThread = null;
        if (this.cameraThreadHandler != null) {
            cameraThread = this.cameraThreadHandler.getLooper().getThread();
        }

        if (cameraThread != null) {
            StackTraceElement[] cameraStackTrace = cameraThread.getStackTrace();
            if (cameraStackTrace.length > 0) {
                Logging.d(TAG, "CameraCapturer stack trace:");
                StackTraceElement[] var3 = cameraStackTrace;
                int var4 = cameraStackTrace.length;

                for (int var5 = 0; var5 < var4; ++var5) {
                    StackTraceElement traceElem = var3[var5];
                    Logging.d(TAG, traceElem.toString());
                }
            }
        }

    }

    private void reportCameraSwitchError(String error, @Nullable CameraSwitchHandler switchEventsHandler) {
        Logging.e(TAG, error);
        if (switchEventsHandler != null) {
            switchEventsHandler.onCameraSwitchError(error);
        }

    }

    private void switchCameraInternal(@Nullable CameraSwitchHandler switchEventsHandler, String selectedCameraName) {
        Logging.d(TAG, "switchCamera internal");
        List<String> deviceNames = Arrays.asList(this.cameraEnumerator.getDeviceNames());
        if (!deviceNames.contains(selectedCameraName)) {
            this.reportCameraSwitchError("Attempted to switch to unknown camera device " + selectedCameraName, switchEventsHandler);
        } else {
            synchronized (this.stateLock) {
                if (this.switchState != SwitchState.IDLE) {
                    this.reportCameraSwitchError("Camera switch already in progress.", switchEventsHandler);
                    return;
                }

                if (!this.sessionOpening && this.currentSession == null) {
                    this.reportCameraSwitchError("switchCamera: camera is not running.", switchEventsHandler);
                    return;
                }

                this.switchEventsHandler = switchEventsHandler;
                if (this.sessionOpening) {
                    this.switchState = SwitchState.PENDING;
                    this.pendingCameraName = selectedCameraName;
                    return;
                }

                this.switchState = SwitchState.IN_PROGRESS;
                Logging.d(TAG, "switchCamera: Stopping session");
                this.cameraStatistics.release();
                this.cameraStatistics = null;
                final MyCamera1Session_32006 oldSession = this.currentSession;
                this.cameraThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        oldSession.stop();
                    }
                });
                this.currentSession = null;
                this.cameraName = selectedCameraName;
                this.sessionOpening = true;
                this.openAttemptsRemaining = 1;
                this.createSessionInternal(0);
            }

            Logging.d(TAG, "switchCamera done");
        }
    }

    private void checkIsOnCameraThread() {
        if (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread()) {
            Logging.e(TAG, "Check is on camera thread failed.");
            throw new RuntimeException("Not on camera thread.");
        }
    }

    protected String getCameraName() {
        synchronized (this.stateLock) {
            return this.cameraName;
        }
    }


    enum SwitchState {
        IDLE,
        PENDING,
        IN_PROGRESS;

        SwitchState() {
        }
    }
}
