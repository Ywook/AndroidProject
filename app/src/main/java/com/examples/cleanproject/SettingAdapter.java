package com.examples.cleanproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Mac on 2017. 5. 26..
 */

public class SettingAdapter extends BaseAdapter {
    String[] data;
    Context context;

    Boolean bAlarm;
    Boolean bVibrate;

    public SettingAdapter(String[] data, Context context, Boolean[] b) {
        this.data = data;
        this.context = context;
        this.bAlarm = b[0];
        this.bVibrate = b[1];
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int i) {
        return data[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int n = i;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.list_item_setting,null);
        }

        TextView tv = (TextView)view.findViewById(R.id.tv);
        Switch sw = (Switch)view.findViewById(R.id.sw);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(n == 2) SettingActivity.soundChecked(b);
                else if(n == 3) SettingActivity.vibrateChecked(b);
            }
        });
        if(!(data[i].equals("알림음") || data[i].equals("진동"))){
            sw.setVisibility(View.GONE);
        }else{
            if(data[i].equals("알림음")) sw.setChecked(bAlarm);
            else sw.setChecked(bVibrate);
        }

        tv.setText(data[i]);
        return view;
    }
}
