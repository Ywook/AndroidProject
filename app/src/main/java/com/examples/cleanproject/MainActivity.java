package com.examples.cleanproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import static android.graphics.Color.rgb;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences setting;
    public static SharedPreferences.Editor editor;


    String SEVER_ADDRESS = LoginActivity.SEVER_ADDRESS;
    BackPressCloseHandler backPressCloseHandler;
    CircularProgressBar[] cpBar;

    Button[] btns;

    String[] d_time = new String[4];
    String[] status = new String[4];
    int[] second = new int[4];
    String TAG_STATUS = "status";
    String TAG_TIME = "time";

    Handler mhandler = new Handler();

    Boolean FIRST = true;
    GetDataJSON g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("사용현황");

        init();

    }

    @Override
    protected void onDestroy() {
        g.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        setImageButton();
        getData(SEVER_ADDRESS+"/fcm/timer/status.php");
        Log.d("rol","onResume호출");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("rol","onpause호출");
        g.cancel(true);
        super.onPause();
    }

    public void init(){
        setting = getSharedPreferences("Clean",0);
        editor = setting.edit();

        backPressCloseHandler = new BackPressCloseHandler(this);

        cpBar = new CircularProgressBar[4];
        cpBar[0] = (CircularProgressBar)findViewById(R.id.circularprogressbar1);
        cpBar[1] = (CircularProgressBar)findViewById(R.id.circularprogressbar2);
        cpBar[2] = (CircularProgressBar)findViewById(R.id.circularprogressbar3);
        cpBar[3] = (CircularProgressBar)findViewById(R.id.circularprogressbar4);

        btns = new Button[2];
        btns[0] = (Button)findViewById(R.id.button1);
        btns[1] = (Button)findViewById(R.id.button2);


        for(int i = 0; i < 4; i++) {
            if(setting.getString((i + 1) + "", "0").equals("0"))
                status[i] = "0";
            else
                status[i] = "1";
        }
        setImageButton();

    }
    public void onClick(View v){
        if(v.getId() == R.id.button1){
            ButtonThread thread = new ButtonThread();
            thread.setNum("1");
            thread.start();
        }else{
            ButtonThread thread = new ButtonThread();
            thread.setNum("2");
            thread.start();

        }
    }

    class ButtonThread extends Thread{
        String num = "";

        void setNum(String n){
            num = n;
        }

        @Override
        public void run() {
            String token = FirebaseInstanceId.getInstance().getToken();
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("token", token)
                    .add("num", num)
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

                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ButtonHandler(num+",1");
                        }
                    });
                }else if(res.equals("0")){
                    request = new Request.Builder()
                            .url(SEVER_ADDRESS+"/fcm/laundry.php")
                            .post(body)
                            .build();
                    client.newCall(request).execute();
                    mhandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ButtonHandler(num+",0");
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ButtonHandler("error");
                    }
                });
            }
        }
    }
    public void ButtonHandler(String s){
        String[] str = s.split(",");
        AlertDialog.Builder alg = new AlertDialog.Builder(MainActivity.this);
        if(s.equals("error")){
            Toast.makeText(getApplicationContext(), "데이터 연결 혹은 서버에러.",Toast.LENGTH_LONG).show();
            return;
        }
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
            editor.putString(str[0], "2");
            editor.commit();
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
            editor.putString(str[0], "1");
            editor.commit();
        }

    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void setImageButton(){
        if(setting.getString("1","0").equals("2")) {
            setButtonImage(0, true);
        }else{
            setButtonImage(0,false);
        }
        if(setting.getString("2","0").equals("2")) {
            setButtonImage(1,true);
        }else{
            setButtonImage(1,false);
        }
    }
    public void setButtonImage(int btn, Boolean b){
        if(b){
            btns[btn].setBackgroundResource(R.drawable.button2);
            btns[btn].setTextColor(rgb(255,255,255));
            btns[btn].setText("알림취소");
        }else{
            btns[btn].setBackgroundResource(R.drawable.button);
            btns[btn].setTextColor(rgb(0,0,0));
            btns[btn].setText("알림받기");
        }
    }

    private void Update(int i){
        final int num = i;

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                if(status[num].equals("0")){
                    cpBar[num].setTitle("0%");
                    cpBar[num].setTitleColor(rgb(228,229,231));
                    cpBar[num].setSubTitle("사용가능");
                    cpBar[num].setSubTitleColor(rgb(24,211,180));
                    cpBar[num].setProgress(0);

                    btns[num].setBackgroundResource(R.drawable.button);
                    btns[num].setTextColor(rgb(228,229,231));
                    btns[num].setText("알림받기");
                    btns[num].setEnabled(false);
                }else{
                    int percent = 100-(int)(((second[num]/60.0)/40)*100);
                    cpBar[num].setTitleColor(rgb(0,137,223));

                    if(percent>100) cpBar[num].setTitle("100%");
                    else cpBar[num].setTitle(percent+"%");

                    cpBar[num].setSubTitleColor(rgb(93,93,93));
                    cpBar[num].setProgress(100-(int)(((second[num]/60.0)/40)*100));

                    if(d_time[num].contains("-")) cpBar[num].setSubTitle("00 : 00");
                    else cpBar[num].setSubTitle(d_time[num]);

                    if(setting.getString((num+1)+"", "0").equals("0")) {
                        btns[num].setBackgroundResource(R.drawable.button);
                        btns[num].setTextColor(Color.rgb(0,0,0));
                        editor.putString((num+1)+"", "1");
                        editor.commit();
                    }

                    btns[num].setEnabled(true);

                }
                for(int j = 2; j <=3; j++){
                    if(status[j].equals("0")){
                        cpBar[j].setTitle("사용가능");
                        cpBar[j].setTitleColor(Color.rgb(24,211,180));
                        cpBar[j].setProgress(0);
                    }else{
                        cpBar[j].setTitle("사용 중");
                        cpBar[j].setTitleColor(Color.rgb(0,137,223));
                        cpBar[j].setProgress(100);
                    }
                }
            }
        };
        mhandler.post(updater);

    }

    public void getData(String url){
        g = new GetDataJSON();
        g.execute(url);
    }

    public void setStatus(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("result");

            for(int i  = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                String temp = object.getString(TAG_STATUS);
                String endtime = object.getString(TAG_TIME);
                if(temp.equals("1")){
                    status[i] = temp;
                    second[i] = remainTime(endtime);
                }else{
                    status[i] = "0";
                    second[i] = 0;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        return super.onOptionsItemSelected(item);
    }

    ProgressDialog progressDialog;

    class GetDataJSON extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            if(FIRST){
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.show();
            }

        }

        @Override
        protected void onCancelled() {
            FIRST = true;
            super.onCancelled();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            if(FIRST){
                FIRST = false;
                progressDialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            while(true){
                OkHttpClient client = new OkHttpClient();

                //request
                Request request = new Request.Builder()
                        .url(strings[0])
                        .build();
                try {
                    Thread.sleep(1000);
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    response.body().close();

                    setStatus(jsonData);
                    for(int i=0; i<2; i++){

                        if (second[i] != 0) {
                            int min = second[i] / 60;
                            int sec = second[i] % 60;
                            d_time[i] = String.format("%02d : %02d", min, sec);
                        } else {
                            d_time[i] = "00 : 00";
                        }
                        Update(i);
                        publishProgress();
                    }
                } catch (Exception e) {
                    publishProgress();
                    e.printStackTrace();
                    return null;
                }
            }

        }

        @Override
        protected void onPostExecute(String s) {
            if(s == null){
                g.cancel(true);
                Toast.makeText(getApplicationContext(), "데이터 연결 혹은 서버에러",Toast.LENGTH_LONG).show();
                return;
            }

        }
    }

}
