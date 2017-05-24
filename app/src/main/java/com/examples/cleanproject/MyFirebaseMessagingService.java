package com.examples.cleanproject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Mac on 2017. 5. 23..
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    String title, body, num;

    public static SharedPreferences setting,pushset;
    public static SharedPreferences.Editor SettingEditor,PushEditor;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("message");
        num = remoteMessage.getData().get("num");

        setting = getSharedPreferences("alarm",0);
        SettingEditor = setting.edit();
        SettingEditor.putString(num,"0");
        SettingEditor.commit();

        sendNotification(title, body);
    }

    private  void sendNotification(String title, String body){
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationB = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(title) //상단바 알림제목
                .setContentText(body) //상단바 알림내용
                .setAutoCancel(true)//터치하면 자동으로 지워지도록 하는 것
                .setSound(defaultSoundUri)   //알림음 설정
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationB.build());

    }
}
