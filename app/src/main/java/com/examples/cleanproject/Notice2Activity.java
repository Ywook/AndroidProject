package com.examples.cleanproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Notice2Activity extends AppCompatActivity {
    TextView title, content, date;

    private SharedPreferences setting;

    String gTitle, gContent, gDate, gId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice2);
        setTitle("공지사항");

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        init();
    }
    void init(){
        setting = getSharedPreferences("Clean", MODE_PRIVATE);

        title = (TextView) findViewById(R.id.card_title);
        content = (TextView) findViewById(R.id.card_content);
        date = (TextView) findViewById(R.id.card_date);

        gTitle = getIntent().getStringExtra("title");
        gContent = getIntent().getStringExtra("content");
        gDate = getIntent().getStringExtra("date");
        gId = getIntent().getStringExtra("id");
        title.setText(gTitle);
        content.setText(gContent);
        date.setText(gDate);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(setting.getString("ID", "").equals("admin")){
            getMenuInflater().inflate(R.menu.menu_notice_modify, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_modify:
                Intent i = new Intent(Notice2Activity.this, NoticeWriteActivity.class);
                i.putExtra("Modify", true);
                i.putExtra("id",gId);
                i.putExtra("title", gTitle);
                i.putExtra("date", gDate);
                i.putExtra("content", gContent);
                startActivity(i);

                break;
        }

        return super.onOptionsItemSelected(item);

    }
}
