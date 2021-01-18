package com.louisgeek.lg_videochat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {
    CountDownTimer mCountDownTimer;
    Button id_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        id_btn = findViewById(R.id.id_btn);

        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(2_000, 1_000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //0.1 修正秒
                    int second = (int) (millisUntilFinished / 1000 + 0.1);
                    id_btn.setText(String.format(Locale.CHINA, "跳过(%d)", second));
                }

                @Override
                public void onFinish() {
                    //设置跳转前最后的显示内容
                    id_btn.setText("跳过(0)");
                    //
                    goToMain();
                }
            };
        }
        mCountDownTimer.start();
    }

    private void goToMain() {
        SettingUsernameActivity.actionStart(this);
        supportFinishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }


}
