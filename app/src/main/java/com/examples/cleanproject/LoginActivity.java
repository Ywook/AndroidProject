package com.examples.cleanproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private BackPressCloseHandler backPressCloseHandler;
    public static String SEVER_ADDRESS = "http://jyw.dothome.co.kr";

    EditText e_id, e_pw;

    public static final int REQUEST_CODE_SINGUP=1001;

    private static final String Connect_ERROR="데이터 연결 혹은 서버 에러.";
    private static final String ID_ERROR = "없는 아이디입니다.";
    private static final String PASSWORD_ERROR = "비밀번호가 일치하지 않습니다.";

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    String eID = "";
    String ePW = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }
    public void init(){
        backPressCloseHandler = new BackPressCloseHandler(this);

        e_id = (EditText)findViewById(R.id.id);
        e_pw = (EditText)findViewById(R.id.password);

    }


    public void onClick(View v){
        if(v.getId() == R.id.signup){
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SINGUP);
        }else if(v.getId() == R.id.login){
            login();
        }
    }

    public void login(){
        eID = e_id.getText().toString();
        ePW = e_pw.getText().toString();

        if(eID.equals("")){
            Toast.makeText(LoginActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
            return;

        }else if(ePW.equals("")){
            Toast.makeText(LoginActivity.this,"비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show();
            return;
        }

        new Asynclogin().execute(eID,ePW);

    }
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private class Asynclogin extends AsyncTask<String,String ,String> {

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .build();

            RequestBody body = new FormBody.Builder()
                    .add("id", strings[0])
                    .add("pw", strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url(SEVER_ADDRESS + "/login.php")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                response.body().close();

                if (res.equalsIgnoreCase("ok")) {
                    return "ok";
                } else if (res.equalsIgnoreCase("id")) {
                    return "id";
                }else if(res.equalsIgnoreCase("pw")){
                    return "pw";
                }else{
                    return res;
                }


            } catch (IOException e) {
                e.printStackTrace();
                return "fail";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("ok")){
                settings = getSharedPreferences("Clean", MODE_PRIVATE);
                editor = settings.edit();
                editor.putString("ID", eID);
                editor.putString("PW", ePW);
                editor.putBoolean("Auto_Login_enabled", true);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }else if(s.equals("id")){
                Toast.makeText(getApplicationContext(), ID_ERROR, Toast.LENGTH_LONG).show();

            }else if(s.equals("pw")) {
                Toast.makeText(getApplicationContext(), PASSWORD_ERROR, Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getApplicationContext(), Connect_ERROR, Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SINGUP){
            if(resultCode == RESULT_OK){
                e_id.setText(data.getStringExtra("id"));
                e_pw.setText(data.getStringExtra("pw"));
            }
        }
    }
}
