package com.examples.cleanproject;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Mac on 2017. 5. 23..
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);

    }
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();
        //request
        Request request = new Request.Builder()
                .url(LoginActivity.SEVER_ADDRESS+"/register.php")
                .post(body)
                .build();
        try {
            Response response=client.newCall(request).execute();
            Log.d("response",response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
