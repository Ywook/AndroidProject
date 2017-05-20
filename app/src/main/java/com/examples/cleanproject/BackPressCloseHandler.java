package com.examples.cleanproject;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by Mac on 2017. 5. 17..
 */

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Activity activity;
    private Toast toast;

    public BackPressCloseHandler(Activity activity) {
        this.activity = activity;
    }

    public void onBackPressed(){
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            showToast();

        }else{
            toast.cancel();

            activity.moveTaskToBack(true); //finish 이전에 호출해서 finish 후 다른 액티비티가 나오는 걸 막는다
            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void showToast(){
        toast = Toast.makeText(activity, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
        toast.show();
    }
}
