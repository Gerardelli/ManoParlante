package com.santiago.talkinghand.retrofit;

import com.santiago.talkinghand.models.FCMBody;
import com.santiago.talkinghand.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAyXukySg:APA91bEf9fufd1cRzSFkvyrx6JE8cAw7RQgqXPLM9FilRuk2_y2ML7LbjHDEUW_PEGi2_cc0455gibnD3ozwCNYnRP1Xnr-fISlsbIgP-i-qw9iMGnwfbuJUeQhwmqQ_79_utoMX45QO"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
