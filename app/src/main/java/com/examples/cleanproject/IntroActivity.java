package com.examples.cleanproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class IntroActivity extends AppCompatActivity {
    Handler handler;

    SharedPreferences auto_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        handler = new Handler();
        handler.postDelayed(mrun, 2000);
    }

    Runnable mrun = new Runnable() {
        @Override
        public void run() {
            Intent intent;
            auto_login = getSharedPreferences("Clean", MODE_PRIVATE);
            if(auto_login.getBoolean("Auto_Login_enabled", false)){
                intent = new Intent(IntroActivity.this, MainActivity.class);
            }else{
                intent = new Intent(IntroActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(mrun);
        /*
        인트로 중에 뒤로가기를 누를 경우 핸드러를 끊어서 아무일 없게 만드는 부분
        미 설정시 인트로 중 뒤로가기를 누르면 인트로 후에 홈 화면이 나오게 된다
         */
    }
}
