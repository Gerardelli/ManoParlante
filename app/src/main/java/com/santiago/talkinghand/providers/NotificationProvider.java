package com.santiago.talkinghand.providers;

import com.santiago.talkinghand.models.FCMBody;
import com.santiago.talkinghand.models.FCMResponse;
import com.santiago.talkinghand.retrofit.IFCMApi;
import com.santiago.talkinghand.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){

    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
