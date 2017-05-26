package com.examples.cleanproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SettingActivity extends AppCompatActivity {
    ListView listView;
    SettingAdapter adapter;
    String[] data = {"공지사항", "문의하기", "알림음", "진동" ,"비밀번호 변경", "로그아웃"};

    static SharedPreferences setting;
    static SharedPreferences.Editor editor;


    Boolean[] b ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("설정");
        init();
    }

    public void init(){
        setting = getSharedPreferences("alarm",MODE_PRIVATE);
        editor = setting.edit();

        b = new Boolean[]{setting.getBoolean("sound", true), setting.getBoolean("vibrate", true)};

        adapter = new SettingAdapter(data,SettingActivity.this, b);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    public static void soundChecked(Boolean b){
        editor.putBoolean("sound",b);
        editor.commit();
    }
    public static void vibrateChecked(Boolean b){
        editor.putBoolean("vibrate",b);
        editor.commit();
    }
}
