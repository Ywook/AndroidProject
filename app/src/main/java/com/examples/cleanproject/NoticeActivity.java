package com.examples.cleanproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.examples.cleanproject.LoginActivity.SEVER_ADDRESS;

public class NoticeActivity extends AppCompatActivity {
    String SEVER_ADDRSS = SEVER_ADDRESS + "/notice/read.php";

    private SharedPreferences setting;

    private ArrayList<NoticeData> data = new ArrayList<>();
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private String admin;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(setting.getString("ID", "").equals("admin")){
            getMenuInflater().inflate(R.menu.menu_write,menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_write){
            Intent intent = new Intent(NoticeActivity.this, NoticeWriteActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        setTitle("공지사항");
        setting = getSharedPreferences("Clean", MODE_PRIVATE);
        admin = setting.getString("ID", "");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLinearLayoutManager);

        adapter = new MyAdapter(data, this);

    }

    @Override
    protected void onResume() {
        data.clear();
        getData(SEVER_ADDRSS);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    public void makeList(String Json) {
        try {
            JSONObject jsonObject = new JSONObject(Json);
            JSONArray Jarray = jsonObject.getJSONArray("result");
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject c = Jarray.getJSONObject(i);
                String title = c.getString("title");
                String date = c.getString("date");
                String content = c.getString("content");
                int id = c.getInt("id");

                data.add(new NoticeData(title, date, content, id));
            }
            recyclerView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    public void getData(String url) {
        class getDataJson extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... s) {
                OkHttpClient client = new OkHttpClient();
                //request
                Request request = new Request.Builder()
                        .url(s[0])
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    response.body().close();
                    return jsonData;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    readFile();
                    Toast.makeText(getApplicationContext(), "데이터 연결 혹은 서버 에러.", Toast.LENGTH_SHORT).show();
                } else {
                    makeList(result);
                    writeFile();
                }
            }
        }
        new getDataJson().execute(url);
    }

    public void readFile(){
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(getFilesDir() +"notice.txt"));
            String[] temp = new String[4];
            String str = null;
            while((str = br.readLine()) != null){
                temp = str.split(",");
                data.add(new NoticeData(temp[0],temp[1],temp[2],Integer.parseInt(temp[3])));

            }
            recyclerView.setAdapter(adapter);
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeFile(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + "notice.txt", false));
            for(int i = 0 ; i <data.size(); i++){
                String temp = data.get(i).getTitle() +","+ data.get(i).getDate() +","+data.get(i).getContent() +","+data.get(i).getId()+"\n";
                bw.write(temp);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<NoticeData> items;

        public MyAdapter(ArrayList<NoticeData> item, Context context) {
            this.items = item;
            this.context = context;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //recyclerView에 반복될 아이템 레이아웃 연결
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.careview_item_notice, null);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String title = items.get(position).getTitle();
            final String date = items.get(position).getDate();
            final String content = items.get(position).getContent();
            final String id = items.get(position).getId()+"";
            Log.d("id123456", id);
            holder.title.setText(title);
            holder.date.setText(date);
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, Notice2Activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Log.d("id123456", id);

                    i.putExtra("id",id);
                    i.putExtra("title", title);
                    i.putExtra("date", date);
                    i.putExtra("content", content);
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView date;
            public CardView cv;

            public ViewHolder(View itemView) {
                super(itemView);
                this.title = (TextView) itemView.findViewById(R.id.tv_title);
                this.date = (TextView) itemView.findViewById(R.id.tv_date);
                this.cv = (CardView) itemView.findViewById(R.id.card_view);

                this.cv.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(!admin.equals("admin")) return false;
                        AlertDialog.Builder alg = new AlertDialog.Builder(context);
                        alg.setTitle("삭제")
                                .setMessage("삭제하시겠습니까?")
                                .setNegativeButton("취소", null)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        index = getAdapterPosition();
                                        new AsyncDelete().execute(data.get(index).getId());
                                    }
                                })
                                .show();
                        return true;
                    }
                });
            }
        }
    }

    ProgressDialog progressDialog;
    private int index;

    class AsyncDelete extends AsyncTask<Integer, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(NoticeActivity.this);
            progressDialog.setMessage("Loading..");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }


        @Override
        protected String doInBackground(Integer... integers) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("id", integers[0] + "")
                    .build();

            Request request = new Request.Builder()
                    .url(SEVER_ADDRESS + "/notice/delete.php")
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
            if(s == null){
                Toast.makeText(getApplicationContext(),"데이터 연결을 혹은 서버 에러",Toast.LENGTH_SHORT).show();
            }else if(s.equals("ok")){
                Toast.makeText(getApplicationContext(), "삭제되었습니다.",Toast.LENGTH_SHORT).show();
                data.remove(index);
                adapter.notifyDataSetChanged();
            }else{
                Toast.makeText(getApplicationContext(),"서버 에러",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

