package com.examples.cleanproject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    String SEVER_ADDRESS = LoginActivity.SEVER_ADDRESS;
    BackPressCloseHandler backPressCloseHandler;

    Button[] btns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("사용현황");

        init();
    }

    public void init(){
        backPressCloseHandler = new BackPressCloseHandler(this);

        btns = new Button[2];
        btns[0] = (Button)findViewById(R.id.button1);
        btns[1] = (Button)findViewById(R.id.button2);

        btns[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncAlarm().execute("1");
            }
        });

        btns[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncAlarm().execute("2");
            }
        });


    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class AsyncAlarm extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            String token = FirebaseInstanceId.getInstance().getToken();
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("token", token)
                    .add("num", strings[0])
                    .build();

            Request request = new Request.Builder()
                    .url(SEVER_ADDRESS + "/fcm/confirm.php")
                    .post(body)
                    .build();

            try{
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                response.body().close();

                if(res.equals("1")){
                    request = new Request.Builder()
                            .url(SEVER_ADDRESS + "/fcm/delete.php")
                            .post(body)
                            .build();
                    client.newCall(request).execute();

                    return strings[0]+",1";
                }else if(res.equals("0")){
                    request = new Request.Builder()
                            .url(SEVER_ADDRESS+"/fcm/laundry.php")
                            .post(body)
                            .build();
                    client.newCall(request).execute();

                    return strings[0] + ",0";
                }else{
                    return res;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            String[] str = s.split(",");
            AlertDialog.Builder alg = new AlertDialog.Builder(MainActivity.this);

            if(str[1].equals("0")){
                alg.setTitle("알림")
                        .setMessage(str[0] + "번 세탁기 알림을 받습니다.")
                        .setPositiveButton("확인", null)
                        .show();

                if(str[0].equals("1")){
                    setButtonImage(0,true);
                }else{
                    setButtonImage(1,true);
                }
            }else if(str[1].equals("1")){
                alg.setTitle("알림")
                        .setMessage(str[0] + "번 세탁기 알림이 취소되었습니다.")
                        .setPositiveButton("확인", null)
                        .show();
                if(str[0].equals("1")){
                    setButtonImage(0,false);
                }else{
                    setButtonImage(1,false);
                }

            }else if(s.equals("error")){
                Toast.makeText(getApplicationContext(), "데이터 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(),"서버 에러", Toast.LENGTH_LONG).show();
            }

        }
    }
    public void setButtonImage(int btn, Boolean b){
        if(b){
            btns[btn].setBackgroundResource(R.drawable.button2);
            btns[btn].setTextColor(Color.rgb(255,255,255));
            btns[btn].setText("알림취소");
        }else{
            btns[btn].setBackgroundResource(R.drawable.button);
            btns[btn].setTextColor(Color.rgb(0,0,0));
            btns[btn].setText("알림받기");
        }
    }

    public int remainTime(String timeEnd){
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.KOREA);
        try{
            String timeNow = dataFormat.format(date);
            Date startTime = dataFormat.parse(timeNow);
            Date endTime = dataFormat.parse(timeEnd);

            long mils = (endTime.getTime() - startTime.getTime());
            int second = (int)mils/1000;
            return second;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

}
