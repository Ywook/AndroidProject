package com.examples.cleanproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText etId, etPW, etPW2;
    String SEVER_ADDRESS = "http://192.168.43.148:8080";
    private static final String CONNECT_ERROR = "데이터 연결을 확인해주세요.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    void init() {
        etId = (EditText) findViewById(R.id.idInput);
        etPW = (EditText) findViewById(R.id.passwordInput);
        etPW2 = (EditText) findViewById(R.id.passwordInput2);

    }

    public void onClick(View v) {


        if (etId.getText().length() < 4) {
            Toast.makeText(getApplicationContext(), "아이디가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else if (etPW.getText().length() < 4) {
            Toast.makeText(getApplicationContext(), "비밀번호가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        String id = etId.getText().toString();
        String pw = etPW.getText().toString();
        String pw2 = etPW2.getText().toString();


        if (!pw.equals(pw2)) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AsyncSignUp().execute(id, pw);
    }

    private class AsyncSignUp extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("id", strings[0])
                    .add("pw", strings[1])
                    .build();


            Request request = new Request.Builder()
                    .url(SEVER_ADDRESS + "/join.php")
                    .post(body)
                    .build();

            Log.d("idpwbody", request.body().toString()+ "ok");
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                Log.d("idpwres", res);
                response.body().close();

                if (res.equalsIgnoreCase("ok")) {
                    Intent intent = new Intent();

                    intent.putExtra("id", strings[0]);
                    intent.putExtra("pw", strings[1]);

                    setResult(RESULT_OK, intent);
                    return "ok";
                } else if (res.equalsIgnoreCase("exist")) {
                    return "exist";
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equalsIgnoreCase("ok")) {
                Toast.makeText(getApplicationContext(), "가입완료", Toast.LENGTH_SHORT).show();
                finish();
            } else if (s.equals("exist")) {
                Toast.makeText(getApplicationContext(), "아이디 중복입니다.", Toast.LENGTH_SHORT).show();
            }  else{
                Toast.makeText(getApplicationContext(), CONNECT_ERROR, Toast.LENGTH_LONG).show();
            }
        }
    }
}
