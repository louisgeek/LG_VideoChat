package com.louisgeek.chat.video;

import android.util.Log;

import com.louisgeek.chat.video.model.base.UserModel;
import com.louisgeek.chat.video2.DeviceStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by louisgeek on 2019/10/19.
 */
public class CallVideoHelper {
    private static final String TAG = "VideoHelper";
    public static final String videoChatModelJson = "videoChatModelJson";
    public static final String ViewStatusCallIn = "ViewStatusCallIn";
    public static final String ViewStatusCallOut = "ViewStatusCallOut";
    public static final String ViewStatusCatting = "ViewStatusCatting";
    public static final String ViewStatusSwitch2Video = "ViewStatusSwitch2Video";
    public static final String ViewStatusSwitch2Audio = "ViewStatusSwitch2Audio";
    //

    public static final String CameraStatus_None = "CameraStatus_None";
    public static final String CameraStatus_COMM = "CameraStatus_COMM";
    public static final String CameraStatus_BACK = "CameraStatus_BACK";
    public static final String CameraStatus_FRONT = "CameraStatus_FRONT";

    private static String mSocketUrl;

    /**
     * 本设备 userModel
     */
    public static UserModel userModel;
    public static UserModel otherUserModel;
//    public static ChatInfoModel<VideoChatConfigModel> chatInfoModel;
    /*
   public static UserModel getToUserModel(ChatInfoModel<VideoChatConfigModel> chatInfoModel) {
        if (chatInfoModel != null && userModel != null) {
            //chatInfoModel.fromUserModel 代表呼叫的发起方
            if (ObjectsCompat.equals(chatInfoModel.fromUserModel.userId, userModel.userId)) {
                return chatInfoModel.toUserModel;
            }
        }
        return chatInfoModel.fromUserModel;
    }*/


    public static void updateSocketUrl(String socketUrl) {
        if (!socketUrl.endsWith("/")) {
            //为了防止结尾/影响后面判断结果
            socketUrl = socketUrl + "/";
        }
        mSocketUrl = mSocketUrl == null ? "" : mSocketUrl;
        if (!mSocketUrl.endsWith("/")) {
            //为了防止结尾/影响后面判断结果
            mSocketUrl = mSocketUrl + "/";
        }
        if (!mSocketUrl.equals(socketUrl)) {
//            SocketClient.get().releaseSocket();
            mSocketUrl = socketUrl;
            socketUserLogin();
        }
    }

    public static void socketUserLogin() {
        Log.e(TAG, "goToUserLogin: socketUrl " + mSocketUrl);
       /* String roomModelJson = new Gson().toJson(mRoomModel);
        String userModelJson = new Gson().toJson(mUserModel);
        MessageSocketAdapter.userLogin(mSocketUrl, userModelJson, roomModelJson);*/
    }

    public static void socketUserLogout() {
        Log.e(TAG, "socketUserLogout: socketUrl " + mSocketUrl);
    }

    /*public static final List<Pair2<String, String>> SETTING_VOLUME_CALL_TYPE = new ArrayList<>();

    static {
        SETTING_VOLUME_CALL_TYPE.add(Pair2.create("0", "普通呼叫"));
        SETTING_VOLUME_CALL_TYPE.add(Pair2.create("1", "增援呼叫"));
        SETTING_VOLUME_CALL_TYPE.add(Pair2.create("2", "紧急呼叫"));
    }*/

    public static final String SETTING_VOLUME_BEAN_LIST_JSON = "SETTING_VOLUME_BEAN_LIST_JSON";
    public static boolean isUserLoginSuccess;
    public static boolean isVideo;
    public static String locationedUserId;
    public static final int DEFAULT_VOLUME = 40;
    public static DeviceStatus mDeviceStatus;
    public static Map<String, DeviceStatus> mNurseStationCallStatus = new ConcurrentHashMap<>();//护士站呼叫列表状态

    public static boolean isBeBusy() {
        boolean isBusy = CallVideoHelper.mDeviceStatus == DeviceStatus.CallIn
                || CallVideoHelper.mDeviceStatus == DeviceStatus.CallOut
                || CallVideoHelper.mDeviceStatus == DeviceStatus.Chatting
                || CallVideoHelper.mDeviceStatus == DeviceStatus.CallInWay
                || CallVideoHelper.locationedUserId != null;
        return isBusy;
    }

    //护士站通话中
    public static boolean isNurseStationChatting() {
        boolean isChatting = false;
        for (DeviceStatus status : mNurseStationCallStatus.values()) {
            if (status == DeviceStatus.Chatting) {
                isChatting = true;
                break;
            }
        }
        return isChatting;
    }

    //护士站呼出中
    public static boolean isNurseStationCallOut() {
        boolean isCallOut = false;
        for (DeviceStatus status : mNurseStationCallStatus.values()) {
            if (status == DeviceStatus.CallOut) {
                isCallOut = true;
                break;
            }
        }
        return isCallOut;
    }

    //护士站忙碌中
    public static boolean isNurseStationBusy() {
        return CallVideoHelper.locationedUserId != null || mNurseStationCallStatus.size() > 0;
    }

    public interface OnlineBack {
        void online(boolean isOnline);
    }
}
