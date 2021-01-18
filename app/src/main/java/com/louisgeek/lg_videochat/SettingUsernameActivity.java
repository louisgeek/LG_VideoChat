package com.louisgeek.lg_videochat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mmkv.MMKV;


@Deprecated
public class SettingUsernameActivity extends AppCompatActivity {
    private static final String TAG = "SettingUsernameActivity";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    private String userId;
    private String userName;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SettingUsernameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_username);
//
        userId = MMKV.defaultMMKV().getString(USER_ID, "");
        userName = MMKV.defaultMMKV().getString(USER_NAME, "");
        Log.e(TAG, "onCreate:userId " + userId);
        Log.e(TAG, "onCreate:userName " + userName);
        if (TextUtils.isEmpty(userId)) {
            EditText id_et_userid = findViewById(R.id.id_et_userid);
            EditText id_et_username = findViewById(R.id.id_et_username);
            Button id_btn_ok = findViewById(R.id.id_btn_ok);
            id_btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                    userId = id_et_userid.getText().toString();
                    userName = id_et_username.getText().toString();
                    //
                    MMKV.defaultMMKV().putString(USER_ID, userId);
                    MMKV.defaultMMKV().putString(USER_NAME, userName);
                    //
                    goToUserList();
                }
            });
        } else {
            goToUserList();
        }


    }

    private void goToUserList() {
        UserListActivity.actionStart(this, userId, userName);
        finish();
    }
}