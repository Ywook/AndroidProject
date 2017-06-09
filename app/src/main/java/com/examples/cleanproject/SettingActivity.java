package com.examples.cleanproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SettingActivity extends AppCompatActivity {
    ListView listView;
    SettingAdapter adapter;
    String[] data = {"공지사항", "문의하기", "알림음", "진동" , "로그아웃"};
    String id = "";
    MainActivity mActivity = (MainActivity) MainActivity.mainActivity;

    static SharedPreferences setting;
    static SharedPreferences.Editor editor;

    int Notice = 0, QA = 1, OUT = 4 ;

    Boolean[] b ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("설정");
        init();
    }

    public void init(){
        setting = getSharedPreferences("Clean",MODE_PRIVATE);
        editor = setting.edit();
        id = setting.getString("ID","");
        b = new Boolean[]{setting.getBoolean("sound", true), setting.getBoolean("vibrate", true)};

        adapter = new SettingAdapter(data,SettingActivity.this, b);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("index of List", ": "+i);
                if(i == Notice){
                    Intent intent = new Intent(SettingActivity.this, NoticeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }else if(i == QA){
                    if(id.equals("admin")){
                        Intent intent = new Intent(SettingActivity.this, ChatRoomActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(SettingActivity.this, ChatRoom.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("room_name", id);
                        intent.putExtra("user_name",id);
                        startActivity(intent);
                    }

                }else if(i == OUT){
                    editor.putString("ID", "");
                    editor.putString("PW", "");
                    editor.putBoolean("Auto_Login_enabled", false);
                    editor.commit();

                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    mActivity.finish();
                }
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
