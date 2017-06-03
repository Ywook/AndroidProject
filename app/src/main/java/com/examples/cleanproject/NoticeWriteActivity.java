package com.examples.cleanproject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.examples.cleanproject.LoginActivity.SEVER_ADDRESS;

public class NoticeWriteActivity extends AppCompatActivity {
    String id ="";
    String SEVER_ADDRSS = SEVER_ADDRESS + "/notice";

    EditText title, content;

    TextView mDate;

    Boolean Modify;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        Modify = getIntent().getBooleanExtra("Modify", false);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (EditText) findViewById(R.id.et_title);
        content = (EditText) findViewById(R.id.et_content);
        mDate = (TextView) findViewById(R.id.date);

        if (Modify) {
            init();
        } else {
            init2();
        }

    }

    public void init(){
        setTitle("공지사항 수정");

        String t = getIntent().getStringExtra("title");
        String c = getIntent().getStringExtra("content");
        String d = getIntent().getStringExtra("date");
        id = getIntent().getStringExtra("id");

        title.setText(t);
        content.setText(c);
        mDate.setText(d);

    }
    public void init2(){
        setTitle("공지사항 작성");


        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        String strNow = dataFormat.format(date);

        mDate.setText(strNow);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_write_ok, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.write_ok:
                if (Modify) {
                    new AsyncModify().execute(title.getText().toString(), content.getText().toString(),id);
                } else {
                    new AsyncWrite().execute(title.getText().toString(), content.getText().toString(), mDate.getText().toString());
                }
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressDialog progressDialog;

    class AsyncModify extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NoticeWriteActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("title", params[0])
                    .add("content", params[1])
                    .add("id",params[2])
                    .build();

            Request request = new Request.Builder()
                    .url(SEVER_ADDRSS+"/modify.php")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                response.body().close();
                return res;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s == null) {
                Toast.makeText(getApplicationContext(), "데이터 연결 혹은 서버 에러.", Toast.LENGTH_LONG).show();
            } else if (s.equalsIgnoreCase("ok")) {
                Toast.makeText(getApplicationContext(), "수정완료", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(getApplicationContext(), "서버 에러.", Toast.LENGTH_LONG).show();
            }
        }

    }
    class AsyncWrite extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NoticeWriteActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("title", params[0])
                    .add("content", params[1])
                    .add("date", params[2])
                    .build();

            Request request = new Request.Builder()
                    .url(SEVER_ADDRSS+"/write.php")
                    .post(body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                response.body().close();
                return res;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if (s== null) {
                Toast.makeText(getApplicationContext(), "데이터 연결 혹은 서버 에러.", Toast.LENGTH_LONG).show();

            } else if (s.equalsIgnoreCase("ok")) {
                Toast.makeText(getApplicationContext(), "작성완료", Toast.LENGTH_SHORT).show();
                finish();
            } else{
                Toast.makeText(getApplicationContext(), "서버 에러.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
