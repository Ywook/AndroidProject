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
            if(auto_login.getBoolean("Auto_login", false)){
                intent = new Intent(IntroActivity.this, MainActivity.class);
            }else{
                intent = new Intent(IntroActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();

        }
    };
    @Override
    public void finish(){
        super.finish();
        this.overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
        /*화면 전환 애니메이션을 구현하기 위한 메소드 첫번째는 새로 띄워지는 액티비티에 적용할 애니메이션
        두번째는 자신이 종료될 때 적용할 애니메이션
         */
    }
}
