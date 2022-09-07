package com.miji.solar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // splash 화면에서 2초뒤 main 화면으로 넘어가도록 설정
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(getApplicationContext(), MijiMainActivity.class);
                startActivity(main);
                finish();
            }
        }, 2000);

    }
}